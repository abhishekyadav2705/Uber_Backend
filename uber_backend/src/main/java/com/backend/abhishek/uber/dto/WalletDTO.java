package com.backend.abhishek.uber.dto;

import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.WalletTransaction;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.List;

@Data
public class WalletDTO {

    private Long id;

    private UserDto user;

    private Double balance;

    private List<WalletTransactionDTO> transactions;

}
