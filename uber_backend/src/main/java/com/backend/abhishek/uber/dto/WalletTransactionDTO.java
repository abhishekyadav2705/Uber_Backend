package com.backend.abhishek.uber.dto;

import com.backend.abhishek.uber.entities.Ride;
import com.backend.abhishek.uber.entities.Wallet;
import com.backend.abhishek.uber.entities.enums.TransactionMethod;
import com.backend.abhishek.uber.entities.enums.TransactionType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class WalletTransactionDTO {

    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private RideDto ride;

    private String transactionId;

    private WalletDTO wallet;

    private LocalDateTime timeStamp;
}
