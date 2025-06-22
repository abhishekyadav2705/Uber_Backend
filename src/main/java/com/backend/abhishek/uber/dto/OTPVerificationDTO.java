package com.backend.abhishek.uber.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OTPVerificationDTO {
        private String email;
        private String otp;
}
