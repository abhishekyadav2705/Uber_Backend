package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.dto.WalletTransactionDTO;
import com.backend.abhishek.uber.entities.WalletTransaction;

public interface WalletTransactionService {

    void creteNewWalletTransaction(WalletTransaction walletTransaction);
}
