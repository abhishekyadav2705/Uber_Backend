package com.backend.abhishek.uber.repositories;

import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findByUser(User user);
}
