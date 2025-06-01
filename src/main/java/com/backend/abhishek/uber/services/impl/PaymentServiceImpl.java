package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.enums.PaymentStatus;
import com.backend.abhishek.uber.repositories.PaymentRepository;
import com.backend.abhishek.uber.services.PaymentService;
import com.backend.abhishek.uber.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;

    @Override
    public void processPayment(Ride ride) {
        Payment payment = paymentRepository.findByRide(ride).orElseThrow(
                ()->new RuntimeException("Payment not found with ride id "+ride.getId()));
        paymentStrategyManager.paymentStrategy(payment.getPaymentMethod()).processPayment(payment);
    }

    @Override
    public Payment createNewPayment(Ride ride) {
        Payment payment = Payment.builder()
                .ride(ride)
                .paymentMethod(ride.getPaymentMethod())
                .amount(ride.getFare())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus) {
     payment.setPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
    }
}
