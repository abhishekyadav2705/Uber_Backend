package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.entities.WalletTransaction;
import com.backend.abhishek.uber.repositories.WalletTransactionRepository;
import com.backend.abhishek.uber.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper modelMapper;

//    @Override
//    public void creteNewWalletTransaction(WalletTransaction walletTransaction) {
//        walletTransactionRepository.save(walletTransaction);
//    }

    public void creteNewWalletTransaction(WalletTransaction walletTransaction) {
        // Example: assuming transactionId is unique for each transaction
        Optional<WalletTransaction> existingTransaction = walletTransactionRepository
                .findByTransactionId(walletTransaction.getTransactionId());

        if (existingTransaction.isPresent()) {
            // Reuse the existing transaction
            System.out.println("Transaction already exists: " + existingTransaction.get());
        } else {
            // Save the new transaction
            walletTransactionRepository.save(walletTransaction);
            System.out.println("New transaction created: " + walletTransaction);
        }
    }

}
