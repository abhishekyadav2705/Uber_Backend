package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import com.backend.abhishek.uber.exceptions.ResourceNotFoundException;
import com.backend.abhishek.uber.repositories.RideRequestRepository;
import com.backend.abhishek.uber.services.RideRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    @Transactional
    public void expireRideRequest(Long rideRequestId) {
        Optional<RideRequest> optional = rideRequestRepository.findById(rideRequestId);
        if (optional.isPresent()) {
            RideRequest rideRequest = optional.get();
            if (rideRequest.getRideRequestStatus() == RideRequestStatus.PENDING) {
                rideRequest.setRideRequestStatus(RideRequestStatus.EXPIRED);
                rideRequestRepository.save(rideRequest);
                System.out.println("Ride request expired: " + rideRequestId);
            }
        }
    }

}
