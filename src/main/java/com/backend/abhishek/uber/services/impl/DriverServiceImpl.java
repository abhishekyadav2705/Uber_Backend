package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.RideDto;
import com.backend.abhishek.uber.dto.RiderDto;
import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import com.backend.abhishek.uber.repositories.DriverRepository;
import com.backend.abhishek.uber.services.*;
import com.backend.abhishek.uber.utils.EmailContentBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService  rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;
    private final EmailSenderService emailSenderService;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException("Ride Request cannot be accepted, status is "+rideRequest.getRideRequestStatus());
        }
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        rideRequestService.update(rideRequest);
        Driver currentDriver = getCurrentDriver();
        Driver savedDriver = driverRepository.save(currentDriver);
        if(!currentDriver.getAvailable()){
           throw new RuntimeException("Driver is not available,  ride cannot be accepted");
        }
        currentDriver.setAvailable(false);
        currentDriver.setMobileNumber(currentDriver.getMobileNumber());
        Ride ride = rideService.createNewRide(rideRequest,savedDriver);
        String riderEmailForOtp = ride.getRider().getUser().getEmail();
        String otp=ride.getOtp();
        sendOTPOnEmailToRider(riderEmailForOtp,otp);
        return modelMapper.map(ride,RideDto.class);
    }

    private void sendOTPOnEmailToRider(String riderEmailForOtp, String otp) {
        String html  = EmailContentBuilder.buildOtpEmail(otp,"OTP to Confirm ride",LocalDateTime.now());
        emailSenderService.sendOtpEmail(riderEmailForOtp,html);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver= getCurrentDriver();

        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start the ride as he has not accepted earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride cannot be cancelled, invalid status "+ride.getRideStatus());
        }
        rideService.updateRideStatus(ride,RideStatus.CANCELLED);
        //after cancelling the ride make driver available
        driver.setAvailable(true);
        driverRepository.save(driver);
        return modelMapper.map(ride,RideDto.class);
    }

    @Override
    public RideDto startRide(Long rideId, String otp) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver= getCurrentDriver();

        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start the ride as he has not accepted earlier");
        }
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride status is not CONFIRMED, hence cannot be started "+ ride.getRideStatus());
        }
        if(!otp.equals(ride.getOtp())){
           throw new RuntimeException("OTP does not matches"+ otp);
        }
        ride.setStartedAt(LocalDateTime.now());

        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ONGOING);

        paymentService.createNewPayment(savedRide);
        ratingService.createNewRide(savedRide);

        //making the driver to user location to start the ride
        System.out.println("Pickup: " + ride.getPickupLocation());
        System.out.println("Before update: " + driver.getCurrentLocation());
        driver.setCurrentLocation(ride.getPickupLocation());
        driverRepository.save(driver);
        System.out.println("After update: " + driver.getCurrentLocation());

        return modelMapper.map(savedRide,RideDto.class);
    }

    @Override
    @Transactional
    public RideDto endRide(Long rideId) {

        Ride ride = rideService.getRideById(rideId);
        Driver driver= getCurrentDriver();

        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot end the ride as he has not accepted earlier");
        }
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException("Ride status is not Ongoing, hence cannot be ended "+ ride.getRideStatus());
        }
        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ENDED);
        updateDriverAvailability(driver,true);
        paymentService.processPayment(ride);

        //sendEmail once ride is ended
//        applicationEventPublisher.publishEvent(new RideEndedEvent(this, ride.getId(), ride.getTransactionDetails()));


        return modelMapper.map(savedRide,RideDto.class);
    }

    private void updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        driverRepository.save(driver);

    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver= getCurrentDriver();

        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot rate the ride, as the ride does not belong to him");
        }
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Ride cannot be rated, please rate once it is ENDED "+ ride.getRideStatus());
        }
        ratingService.rateRider(ride,rating);
        return ratingService.rateRider(ride,rating);
    }

    @Override
    public DriverDto getMyProfile() {
        Driver driver = getCurrentDriver();
        return modelMapper.map(driver,DriverDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Driver currentDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver,pageRequest).
                map(ride -> modelMapper.map(ride,RideDto.class));
    }

    @Override
    public Driver getCurrentDriver() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return driverRepository.findByUser(user)
                .orElseThrow((()->new RuntimeException("Driver with id  "+user.getId()+" not found!!!")));
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
