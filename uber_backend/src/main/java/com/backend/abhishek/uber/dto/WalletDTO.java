package com.backend.abhishek.uber.dto;

import lombok.Data;

import java.util.List;

@Data
public class WalletDTO {

    private Long id;

    private UserDto user;

    private Double balance;

    private List<WalletTransactionDTO> transactions;

}
