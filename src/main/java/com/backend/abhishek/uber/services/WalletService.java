package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.Wallet;
import com.backend.abhishek.uber.entities.enums.TransactionMethod;

public interface WalletService {

    Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);

    Wallet deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);

    void withdrawMoneyFromWallet();

    Wallet findWalletFromId(Long walletId);

    Wallet createNewWallet(User user);

    Wallet findByUser(User user);
}
