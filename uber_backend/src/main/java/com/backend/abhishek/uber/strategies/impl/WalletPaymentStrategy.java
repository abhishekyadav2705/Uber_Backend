package com.backend.abhishek.uber.strategies.impl;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Payment;
import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.entities.enums.PaymentStatus;
import com.backend.abhishek.uber.entities.enums.TransactionMethod;
import com.backend.abhishek.uber.repositories.PaymentRepository;
import com.backend.abhishek.uber.services.PaymentService;
import com.backend.abhishek.uber.services.WalletService;
import com.backend.abhishek.uber.services.impl.WalletServiceImpl;
import com.backend.abhishek.uber.strategies.PaymentStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

//Rider has 232, Driver has 500
//Ride cost is 100, commission is 30
//Rider -> 232-100 =132
//Driver-> 500+(100-30)= 570

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();
        String riderTxnId = "R_TXN-" + UUID.randomUUID();
        String driverTxnId = "D_TXN-" + UUID.randomUUID();

        walletService.deductMoneyFromWallet(rider.getUser(), payment.getAmount(), riderTxnId, payment.getRide(), TransactionMethod.RIDE);
        double driverCut= payment.getAmount() * (1-PLATFORM_COMMISSION);
        walletService.addMoneyToWallet(driver.getUser(), driverCut, driverTxnId, payment.getRide(), TransactionMethod.RIDE);


//        walletService.deductMoneyFromWallet(rider.getUser(),payment.getAmount(),null,payment.getRide(), TransactionMethod.RIDE);
//        walletService.addMoneyToWallet(driver.getUser(), driverCut, null, payment.getRide(),TransactionMethod.RIDE);

//        paymentService.updatePaymentStatus(payment, PaymentStatus.CONFIRMED);
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);


    }
}
