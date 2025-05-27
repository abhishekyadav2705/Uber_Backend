package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.enums.PaymentStatus;

public interface PaymentService {

    void processPayment(Ride ride);

    Payment createNewPayment(Ride ride);

    void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus);


}
