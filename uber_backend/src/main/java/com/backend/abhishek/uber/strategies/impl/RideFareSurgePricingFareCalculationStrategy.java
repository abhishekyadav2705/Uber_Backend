package com.backend.abhishek.uber.strategies.impl;

import com.backend.abhishek.uber.entities.RideRequest;
import com.backend.abhishek.uber.services.DistanceService;
import com.backend.abhishek.uber.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {

    private final DistanceService distanceService;
    private static final double SURGE_FACTOR = 2;
    private final RestTemplate restTemplate = new RestTemplate();

    private double getSurgeMultiplier(RideRequest rideRequest) {
        double surge = 1.0;

        boolean isHeavyRain = checkRain(rideRequest.getPickupLocation().getX(),
                rideRequest.getPickupLocation().getY());

        boolean isTrafficHigh = false;
        boolean isNightTime = isNightTime(rideRequest.getRequestedTime());
        boolean isWeekendEvening = isWeekendEvening(rideRequest.getRequestedTime());

        if (isHeavyRain) surge *= 1.5;
        if (isNightTime) surge *= 1.2;
        if (isTrafficHigh) surge *= 1.5;
        if (isWeekendEvening) surge *= 1.3;

        return surge;
    }

    private boolean checkRain(double lat, double lon) {
        try {
            String url = "https://wttr.in/" + lat + "," + lon + "?format=j1";
            var response = restTemplate.getForObject(url, java.util.Map.class);

            if (response != null && response.containsKey("current_condition")) {
                var conditions = (java.util.List<?>) response.get("current_condition");
                if (!conditions.isEmpty()) {
                    var current = (java.util.Map<?, ?>) conditions.get(0);
                    String weatherDesc = ((java.util.List<?>) ((java.util.Map<?, ?>) current.get("weatherDesc")).get(0)).get(0).toString();
                    return weatherDesc.toLowerCase().contains("rain");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch weather: " + e.getMessage());
        }
        return false;
    }

    private boolean isNightTime(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return time.isAfter(LocalTime.of(20, 0)) || time.isBefore(LocalTime.of(6, 0));
    }

    private boolean isWeekendEvening(LocalDateTime dateTime) {
        log.info("inside isWeekendEvening");
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        return (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                && time.isAfter(LocalTime.of(18, 0));
    }

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double ratePerKm = 10.0;
        double distance = distanceService.calculateDistance(
                rideRequest.getPickupLocation(), rideRequest.getDropOffLocation());

        double baseFare = distance * ratePerKm;
        double surgeMultiplier = getSurgeMultiplier(rideRequest);

        return baseFare * surgeMultiplier;
    }

//    @Override
//    public double calculateFare(RideRequest rideRequest) {
//        double distance = distanceService.calculateDistance(
//                rideRequest.getPickupLocation(), rideRequest.getDropOffLocation());
//        return distance* ApplicationConstant.RIDE_FARE_MULTIPLIER*SURGE_FACTOR;
//    }
}
