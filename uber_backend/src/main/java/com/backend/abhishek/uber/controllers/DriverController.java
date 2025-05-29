package com.backend.abhishek.uber.controllers;

import com.backend.abhishek.uber.dto.*;
import com.backend.abhishek.uber.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<RideDto> acceptRide(@PathVariable Long rideRequestId){
        return ResponseEntity.ok(driverService.acceptRide(rideRequestId));
    }
    @PostMapping("/startRide/{rideRequestId}")
    public ResponseEntity<RideDto> startRide(@PathVariable Long rideRequestId,
                                             @RequestBody RideStartDto rideStartDto){
        return ResponseEntity.ok(driverService.startRide(rideRequestId, rideStartDto.getOtp()));
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.cancelRide(rideId));
    }

    @PostMapping("/endRide/{rideId}")
    public ResponseEntity<RideDto> endRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.endRide(rideId));
    }

    @PostMapping("/rateDriver")
    public ResponseEntity<RiderDto> rateDriver(@RequestBody RatingDTO ratingDTO){
        return ResponseEntity.ok(driverService.rateRider(ratingDTO.getRideId(),  ratingDTO.getRating()));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<DriverDto>  getMyProfile(){
        return ResponseEntity.ok(driverService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
                                                       @RequestParam(defaultValue = "10",required = false) Integer pageSize){
        PageRequest pageRequest = PageRequest.of(pageOffset,pageSize
        , Sort.by(Sort.Direction.DESC,"createdTime","id"));
        return ResponseEntity.ok(driverService.getAllMyRides(pageRequest));
    }
}
