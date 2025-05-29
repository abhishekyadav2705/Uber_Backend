package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.RiderDto;
import com.backend.abhishek.uber.entities.Ride;

public interface RatingService {
    DriverDto rateDriver(Ride ride, Integer rating);
    RiderDto rateRider(Ride ride , Integer rating);

    void createNewRide(Ride ride);
}
