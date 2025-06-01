package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.RideDto;
import com.backend.abhishek.uber.dto.RideRequestDto;
import com.backend.abhishek.uber.dto.RiderDto;
import com.backend.abhishek.uber.entities.*;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import com.backend.abhishek.uber.repositories.DriverRepository;
import com.backend.abhishek.uber.repositories.RideRequestRepository;
import com.backend.abhishek.uber.repositories.RiderRepository;
import com.backend.abhishek.uber.services.RatingService;
import com.backend.abhishek.uber.services.RideService;
import com.backend.abhishek.uber.services.RiderService;
import com.backend.abhishek.uber.strategies.RideStrategyManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverRepository driverRepository;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        Rider rider = getCurrentRider();
        RideRequest rideRequest = modelMapper.map(rideRequestDto,RideRequest.class);
        log.info(rideRequest.toString());
        rideRequest.setRequestedTime(LocalDateTime.now());

        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        //to calculate fare we need distance for that we are using OSRM API
        double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        List<Driver> drivers =
                rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);
        log.info(drivers.toString());
        //TODO : Send notification to all drivers about this ride request

        return modelMapper.map(savedRideRequest,RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        Rider rider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);
        Driver currentDriver = ride.getDriver();

        if(!rider.equals(ride.getRider())){
           throw new RuntimeException("Rider not own this ride "+rideId);
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride cannot be cancelled, invalid status "+ride.getRideStatus());
        }

        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.CANCELLED);
        //after cancelling the ride make driver available
        currentDriver.setAvailable(true);
        driverRepository.save(currentDriver);

        return modelMapper.map(savedRide,RideDto.class);
    }

    @Override
    public RideRequestDto cancelRideRequest(Long rideRequestId) {
        Optional<RideRequest> rideRequestById = rideRequestRepository.findById(rideRequestId);
        if(rideRequestById.isEmpty()){
            throw new RuntimeException("Ride with id "+rideRequestId+" not  found");
        }
        if(!rideRequestById.get().getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException("Ride is already cancelled "+rideRequestById.get().getRideRequestStatus());
        }
        RideRequest rideRequest = rideRequestById.get();
        rideRequest.setRideRequestStatus(RideRequestStatus.CANCELLED);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);
        return modelMapper.map(savedRideRequest,RideRequestDto.class);
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Rider rider= getCurrentRider();

        if(!rider.equals(ride.getRider())){
            throw new RuntimeException("Rider cannot rate the ride, as the ride does not belong to him");
        }
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Ride cannot be rated, please rate once it is ENDED "+ ride.getRideStatus());
        }
        ratingService.rateRider(ride,rating);
        return ratingService.rateDriver(ride,rating);
    }

    @Override
    public RiderDto getMyProfile() {
        Rider rider =  getCurrentRider();
        return modelMapper.map(rider,RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Rider currentRider = getCurrentRider();
        return rideService.getAllRidesOfRider(currentRider,pageRequest).
                map(ride -> modelMapper.map(ride,RideDto.class));
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .rating(0.0)
                .build();

        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        //TODO : implement spring security
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user).orElseThrow(()-> new RuntimeException("Rider not found with id "+user.getId()));
    }
}
