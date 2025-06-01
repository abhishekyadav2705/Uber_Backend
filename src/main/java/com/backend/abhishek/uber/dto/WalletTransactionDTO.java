package com.backend.abhishek.uber.dto;

import com.backend.abhishek.uber.entities.enums.TransactionMethod;
import com.backend.abhishek.uber.entities.enums.TransactionType;
import lombok.Data;

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

    private boolean isEmailProcessed;

}
