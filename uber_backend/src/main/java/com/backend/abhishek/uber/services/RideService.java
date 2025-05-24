package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RideService {

    Ride getRideById(Long rideId);

    Ride createNewRide(RideRequest rideRequest, Driver driver);

    Ride updateRideStatus(Ride ride, RideStatus rideStatus);

    Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest);

    Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest);
}
