package com.backend.abhishek.uber.dto;

import com.backend.abhishek.uber.entities.enums.PaymentMethod;
import com.backend.abhishek.uber.entities.enums.RideStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideDto {

    private Long id;
    private PointDto pickupLocation;
    private PointDto dropOffLocation;

    private LocalDateTime createdTime;
    private RiderDto rider;
    private DriverDto driver;
    private PaymentMethod paymentMethod;

    private String otp;

    private RideStatus rideStatus;

    private Double fare;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
