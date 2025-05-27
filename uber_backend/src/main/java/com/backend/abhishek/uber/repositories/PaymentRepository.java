package com.backend.abhishek.uber.repositories;

import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByRide(Ride ride);
}
