package com.backend.abhishek.uber.strategies;

import com.backend.abhishek.uber.entities.enums.PaymentMethod;
import com.backend.abhishek.uber.strategies.impl.CashPaymentStrategy;
import com.backend.abhishek.uber.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final WalletPaymentStrategy walletPaymentStrategy;
    private final CashPaymentStrategy cashPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod){
        return switch (paymentMethod){
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
            default -> throw new RuntimeException("Invalid Payment method");
        };
    }

}
