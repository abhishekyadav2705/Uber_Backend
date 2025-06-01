package com.backend.abhishek.uber.repositories;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {


//    @Query(
//            value = "SELECT d.* FROM drivers d " +
//                    "WHERE available = true AND ST_DWithin(d.current_location, :pickupLocation, 10000) " +
//                    "ORDER BY ST_Distance(d.current_location, :pickupLocation) " +
//                    "LIMIT 10",
//            nativeQuery = true
//    )

    // ST_Distance(point1, point2)
    // ST_DWithin(point1, 10000)

    @Query(value = "SELECT d.*, ST_Distance(d.current_location,:pickupLocation) AS distance " +
            " from driver d " +
            " where d.available=true and ST_DWithin(d.current_location,:pickupLocation, 10000)" +
            " order by distance DESC " +
            "limit  10",nativeQuery = true)
    List<Driver> findTenNearestMatchingDrivers(Point pickupLocation);

    @Query(value = "SELECT d.* " +
            "FROM driver d " +
            "WHERE d.available=true and ST_DWithin(d.current_location, :pickupLocation, 15000)" +
            " order by d.rating DESC " +
            "LIMIT 10",nativeQuery = true)
    List<Driver> findTenNearbyTopRatedDrivers(Point pickupLocation);

    Optional<Driver> findByUser(User user);
}
