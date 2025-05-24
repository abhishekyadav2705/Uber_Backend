package com.backend.abhishek.uber.strategies;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.RideRequest;

import java.util.List;

public interface DriverMatchingStrategy {

    List<Driver> findMatchingDriver(RideRequest rideRequest);
}
