package com.backend.abhishek.uber.strategies;


import com.backend.abhishek.uber.entities.Payment;

public interface PaymentStrategy {

    static final double PLATFORM_COMMISSION = 0.3;

    void processPayment(Payment payment);
}
