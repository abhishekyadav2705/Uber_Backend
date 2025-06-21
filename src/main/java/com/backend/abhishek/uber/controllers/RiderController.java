package com.backend.abhishek.uber.controllers;

import com.backend.abhishek.uber.advices.AppLogger;
import com.backend.abhishek.uber.dto.*;
import com.backend.abhishek.uber.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
@Secured("ROLE_RIDER")
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

    @PostMapping("/rateDriver")
    public ResponseEntity<DriverDto> rateDriver(@RequestBody RatingDTO ratingDTO){
        return ResponseEntity.ok(riderService.rateDriver(ratingDTO.getRideId(),  ratingDTO.getRating()));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<RiderDto>  getMyProfile(){
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
                                                       @RequestParam(defaultValue = "10",required = false) Integer pageSize){
        PageRequest pageRequest = PageRequest.of(pageOffset,pageSize,
                Sort.by(Sort.Direction.DESC,"createdTime","id"));
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }
}
