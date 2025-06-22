package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.OnboardDriverDTO;
import com.backend.abhishek.uber.dto.SignupDto;
import com.backend.abhishek.uber.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthService {

    String[] login(String email, String password);

    UserDto signup(SignupDto signupDto);

    DriverDto onboardNewDriver(OnboardDriverDTO onboardDriverDTO);

    String refreshToken(String refresh);

    void initiateSignup(SignupDto signupDto) throws JsonProcessingException;

    UserDto verifySignupOtp(String email, String otp) throws JsonProcessingException;
}
