package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.exceptions.ResourceNotFoundException;
import com.backend.abhishek.uber.repositories.RideRequestRepository;
import com.backend.abhishek.uber.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.
                findById(rideRequestId).orElseThrow(()->
                        new ResourceNotFoundException("Ride not found with id "+rideRequestId));
    }

    @Override
    public void update(RideRequest rideRequest) {
        RideRequest savedRide = rideRequestRepository.findById(rideRequest.getId())
                .orElseThrow(()->new ResourceNotFoundException("Ride not found with id "+rideRequest.getId()));
        rideRequestRepository.save(rideRequest);
    }
}
