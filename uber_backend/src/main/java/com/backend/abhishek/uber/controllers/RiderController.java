package com.backend.abhishek.uber.controllers;

import com.backend.abhishek.uber.dto.RideDto;
import com.backend.abhishek.uber.dto.RideRequestDto;
import com.backend.abhishek.uber.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

//    public RiderController(RiderService riderService) {
//        this.riderService = riderService;
//    } use this  or @RequiredArgsConstructor

    @PostMapping("/requestRide")
    public ResponseEntity<RideRequestDto> requestRide(@RequestBody RideRequestDto rideRequestDto){
        return ResponseEntity.ok(riderService.requestRide(rideRequestDto));
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId){
        return ResponseEntity.ok(riderService.cancelRide(rideId));
    }

    @PostMapping("/cancelRideRequest/{rideRequestId}")
    public ResponseEntity<RideRequestDto> cancelRideRequest(@PathVariable Long rideRequestId){
        return ResponseEntity.ok(riderService.cancelRideRequest(rideRequestId));
    }
}
