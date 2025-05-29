package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.RideDto;
import com.backend.abhishek.uber.dto.RiderDto;
import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import com.backend.abhishek.uber.repositories.DriverRepository;
import com.backend.abhishek.uber.repositories.RideRepository;
import com.backend.abhishek.uber.services.DriverService;
import com.backend.abhishek.uber.services.PaymentService;
import com.backend.abhishek.uber.services.RideRequestService;
import com.backend.abhishek.uber.services.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService  rideService;
    private final ModelMapper modelMapper;
    private final RideRepository rideRepository;
    private final PaymentService paymentService;

    @Override
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException("Ride Request cannot be accepted, status is "+rideRequest.getRideRequestStatus());
        }
        Driver currentDriver = getCurrentDriver();
        Driver savedDriver = driverRepository.save(currentDriver);
        if(!currentDriver.getAvailable()){
           throw new RuntimeException("Driver is not available,  ride cannot be accepted");
        }
        currentDriver.setAvailable(false);
        currentDriver.setMobileNumber(currentDriver.getMobileNumber());
        Ride ride = rideService.createNewRide(rideRequest,savedDriver);
        return modelMapper.map(ride,RideDto.class);
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
            throw new RuntimeException("Ride status is not Ongoing, hence cannot be started "+ ride.getRideStatus());
        }
        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ENDED);
        updateDriverAvailability(driver,true);
        paymentService.processPayment(ride);

        return modelMapper.map(savedRide,RideDto.class);
    }

    private void updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        driverRepository.save(driver);

    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        return null;
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
        return driverRepository.findById(2L)
                .orElseThrow((()->new RuntimeException("Driver with id  "+2+" not found!!!")));
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
