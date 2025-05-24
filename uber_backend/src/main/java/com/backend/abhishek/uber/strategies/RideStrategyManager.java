package com.backend.abhishek.uber.strategies;

import com.backend.abhishek.uber.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.backend.abhishek.uber.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.backend.abhishek.uber.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import com.backend.abhishek.uber.strategies.impl.RiderFareDefaultFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy driverMatchingHighestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy driverMatchingNearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy rideFareSurgePricingFareCalculationStrategy;
    private final RiderFareDefaultFareCalculationStrategy riderFareDefaultFareCalculationStrategy;


    public DriverMatchingStrategy driverMatchingStrategy(double riderRating){
        if(riderRating>=4.7){
            return driverMatchingHighestRatedDriverStrategy;
        }
        return driverMatchingNearestDriverStrategy;
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy(){
        //surgeTime = 6PM to 11PM
        LocalTime startTime = LocalTime.of(18,0);
        LocalTime endTime = LocalTime.of(23,0);
        LocalTime currentTime = LocalTime.now();
        boolean isSurge = currentTime.isAfter(startTime) && currentTime.isBefore(endTime);

        if(isSurge){
            return rideFareSurgePricingFareCalculationStrategy;
        }
        return riderFareDefaultFareCalculationStrategy;
    }
}
