package com.backend.abhishek.uber.strategies.impl;

import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.services.DistanceService;
import com.backend.abhishek.uber.strategies.RideFareCalculationStrategy;
import com.backend.abhishek.uber.utils.ApplicationConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RiderFareDefaultFareCalculationStrategy implements RideFareCalculationStrategy {

    private final DistanceService distanceService;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService.calculateDistance(rideRequest.getPickupLocation(),
                rideRequest.getDropOffLocation());

        return distance*ApplicationConstant.RIDE_FARE_MULTIPLIER;
    }
}
