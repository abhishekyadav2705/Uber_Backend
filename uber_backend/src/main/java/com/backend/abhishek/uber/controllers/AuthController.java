package com.backend.abhishek.uber.controllers;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.OnboardDriverDTO;
import com.backend.abhishek.uber.dto.SignupDto;
import com.backend.abhishek.uber.dto.UserDto;
import com.backend.abhishek.uber.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupDto signupDto){
        return  authService.signup(signupDto);
    }

    @PostMapping("/onboardNewDriver/{userId}")
    public ResponseEntity<DriverDto> onboardNewDriver(@RequestBody OnboardDriverDTO onboardDriverDTO){
        return new ResponseEntity<>(authService.onboardNewDriver(onboardDriverDTO), HttpStatus.CREATED);
    }
}
