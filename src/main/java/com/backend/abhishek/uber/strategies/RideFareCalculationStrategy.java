package com.backend.abhishek.uber.strategies;

import com.backend.abhishek.uber.entities.RideRequest;

public interface RideFareCalculationStrategy {

    double calculateFare(RideRequest rideRequest);

}
