package com.backend.abhishek.uber.repositories;

import com.backend.abhishek.uber.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {

    Optional<WalletTransaction> findByTransactionId(String transactionId);

    List<WalletTransaction> findByIsEmailProcessedFalse();

}
