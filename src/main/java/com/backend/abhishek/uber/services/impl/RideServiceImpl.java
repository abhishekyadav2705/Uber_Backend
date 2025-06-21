package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import com.backend.abhishek.uber.repositories.RideRepository;
import com.backend.abhishek.uber.repositories.RideRequestRepository;
import com.backend.abhishek.uber.services.RideRequestService;
import com.backend.abhishek.uber.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;
    private final RideRequestRepository rideRequestRepository;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride with id "+rideId+" Not found!!!"));
    }

    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        Ride ride = modelMapper.map(rideRequest,Ride.class);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setRider(rideRequest.getRider());
        ride.setOtp(generateRandomOtp());
        ride.setId(rideRequest.getId());
        rideRequestService.update(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        RideRequest rideRequest=rideRequestService.findRideRequestById(ride.getId());
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        rideRequestRepository.save(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRider(rider,pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriver(driver, pageRequest);
    }

    private String generateRandomOtp(){
        Random random = new Random();
        int otpInt = random.nextInt(10000);//0 to  999
        return String.format("%04d",otpInt);
    }
}
