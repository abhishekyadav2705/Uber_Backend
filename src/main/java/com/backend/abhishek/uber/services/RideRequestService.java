package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);

    void expireRideRequest(Long rideRequestId);
}
