package com.backend.abhishek.uber.repositories;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Rating;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

    List<Rating> findByRider(Rider rider);
    List<Rating> findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}
