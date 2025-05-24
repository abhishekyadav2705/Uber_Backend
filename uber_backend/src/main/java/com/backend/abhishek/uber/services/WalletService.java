package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.Wallet;

public interface WalletService {

    Wallet addMoneyToWallet(Long userId, double amount);

    void withdrawMoneyFromWallet();

    Wallet findWalletFromId(Long walletId);
}
