package com.backend.abhishek.uber.dto;

import com.backend.abhishek.uber.entities.enums.PaymentMethod;
import com.backend.abhishek.uber.entities.enums.RideRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    private Long id;

    private PointDto pickupLocation;

    private PointDto dropOffLocation;

    private LocalDateTime requestedTime;

    private RiderDto rider;

    private double fare;

    private PaymentMethod paymentMethod;

    private RideRequestStatus rideRequestStatus;
}
