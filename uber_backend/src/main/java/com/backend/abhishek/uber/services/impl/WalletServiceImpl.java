package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.WalletTransactionDTO;
import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.Wallet;
import com.backend.abhishek.uber.entities.WalletTransaction;
import com.backend.abhishek.uber.entities.enums.TransactionMethod;
import com.backend.abhishek.uber.entities.enums.TransactionType;
import com.backend.abhishek.uber.exceptions.ResourceNotFoundException;
import com.backend.abhishek.uber.repositories.WalletRepository;
import com.backend.abhishek.uber.services.WalletService;
import com.backend.abhishek.uber.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final ModelMapper modelMapper;

    @Override
    public Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance()+amount);

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .ride(ride)
                .wallet(wallet)
                .amount(amount)
                .build();

        walletTransactionService.creteNewWalletTransaction(walletTransaction);


        return walletRepository.save(wallet);
    }

    @Override
    public Wallet deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance()-amount);

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .ride(ride)
                .wallet(wallet)
                .amount(amount)
                .build();

        walletTransactionService.creteNewWalletTransaction(walletTransaction);
//        wallet.getTransactions().add(walletTransaction);
        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletFromId(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(()->new ResourceNotFoundException("Wallet not found with id "+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet =new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user);
    }
}
