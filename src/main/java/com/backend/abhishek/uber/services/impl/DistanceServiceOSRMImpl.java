package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API = "http://router.project-osrm.org/route/v1/driving/";

    @Override
    public double calculateDistance(Point src, Point dest) {
        ///call the third party OSRM API to calculate the  distance
        String uri = src.getX()+","+src.getY()+";"+dest.getX()+","+dest.getY();
        try{
            OSRMResponseDTO osrmResponseDTO= RestClient.builder()
                    .baseUrl(OSRM_API)
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(OSRMResponseDTO.class);

            return osrmResponseDTO.getRoutes().getFirst().getDistance()/1000;
        } catch (Exception e) {
            throw new RuntimeException("Error getting data from OSRM API "+e.getMessage());
        }

    }
}

@Data
class OSRMResponseDTO{
    private List<OSRMRoute> routes;
}

@Data
class OSRMRoute{
    private double distance;

}