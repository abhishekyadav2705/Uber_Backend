package com.backend.abhishek.uber.strategies.impl;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Wallet;
import com.backend.abhishek.uber.entities.enums.PaymentStatus;
import com.backend.abhishek.uber.entities.enums.TransactionMethod;
import com.backend.abhishek.uber.repositories.PaymentRepository;
import com.backend.abhishek.uber.services.PaymentService;
import com.backend.abhishek.uber.services.WalletService;
import com.backend.abhishek.uber.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//Rider->100RS
//Driver-> 70, Deduct 30 from Driver's wallet

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
        Driver driver= payment.getRide().getDriver();
        Wallet driverWallet = walletService.findByUser(driver.getUser());
        double platformCommission = payment.getAmount()*PLATFORM_COMMISSION;
        walletService.deductMoneyFromWallet(driver.getUser(), payment.getAmount(),null,payment.getRide(), TransactionMethod.RIDE);

//        paymentService.updatePaymentStatus(payment, PaymentStatus.CONFIRMED);
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
