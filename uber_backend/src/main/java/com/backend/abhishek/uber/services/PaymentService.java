package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Ride;

public interface PaymentService {

    void processPayment(Payment payment);

    Payment createNewPayment(Ride ride);


}
