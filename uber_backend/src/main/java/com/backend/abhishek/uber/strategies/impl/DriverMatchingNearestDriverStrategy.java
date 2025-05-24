package com.backend.abhishek.uber.strategies.impl;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.repositories.DriverRepository;
import com.backend.abhishek.uber.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        return driverRepository.findTenNearestMatchingDrivers(rideRequest.getPickupLocation());
    }
}
