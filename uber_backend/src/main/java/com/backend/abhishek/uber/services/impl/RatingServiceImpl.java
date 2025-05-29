package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.RiderDto;
import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Rating;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.repositories.DriverRepository;
import com.backend.abhishek.uber.repositories.RatingRepository;
import com.backend.abhishek.uber.repositories.RideRepository;
import com.backend.abhishek.uber.repositories.RiderRepository;
import com.backend.abhishek.uber.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverDto rateDriver(Ride ride,  Integer rating) {
        Driver driver=ride.getDriver();
        Rating ratingObj = ratingRepository
                .findByRide(ride).orElseThrow(()-> new RuntimeException("Ride with id "+ride.getId()+" not found!!!"));
        if(ratingObj.getDriverRating()!=null)
            throw new RuntimeException("Rider has already been rated");

        ratingObj.setDriverRating(rating);
        ratingRepository.save(ratingObj);

        Double newRating = ratingRepository.findByDriver(driver)
                .stream().mapToDouble(Rating::getDriverRating)
                .average().orElse(0.0);
        driver.setRating(newRating);

        Driver savedDriver = driverRepository.save(driver);
        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public RiderDto rateRider(Ride ride, Integer rating) {
        Rider rider = ride.getRider();
        Rating ratingObj = ratingRepository
                .findByRide(ride).orElseThrow(()-> new RuntimeException("Ride with id "+ride.getId()+" not found!!!"));

        if(ratingObj.getRiderRating()!=null)
            throw new RuntimeException("Rider has already been rated");

        ratingObj.setRiderRating(rating);
        ratingRepository.save(ratingObj);

        Double newRating = ratingRepository.findByRider(rider)
                .stream().mapToDouble(Rating::getRiderRating)
                .average().orElse(0.0);
        rider.setRating(newRating);

        Rider savedRider = riderRepository.save(rider);
        return modelMapper.map(savedRider, RiderDto.class);
    }

    @Override
    public void createNewRide(Ride ride) {
        Rating rating = Rating.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .build();

        ratingRepository.save(rating);
    }
}
