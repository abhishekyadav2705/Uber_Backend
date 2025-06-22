package com.backend.abhishek.uber.controllers;

import com.backend.abhishek.uber.dto.*;
import com.backend.abhishek.uber.services.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupDto signupDto){
        return  authService.signup(signupDto);
    }

    @PostMapping("/initiate-signup")
    public ResponseEntity<?> initiateSignup(@RequestBody SignupDto signupDto) throws JsonProcessingException {
        authService.initiateSignup(signupDto);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + signupDto.getEmail()));
    }

    @PostMapping("/verify-signup")
    public ResponseEntity<UserDto> verifyOtp(@RequestBody OTPVerificationDTO dto) throws JsonProcessingException {
        UserDto userDto = authService.verifySignupOtp(dto.getEmail(), dto.getOtp());
        return ResponseEntity.ok(userDto);
    }


    //    @Secured("ROLE_ADMIN")
    @PostMapping("/onboardNewDriver")
    public ResponseEntity<DriverDto> onboardNewDriver(@RequestBody OnboardDriverDTO onboardDriverDTO){
        return new ResponseEntity<>(authService.onboardNewDriver(onboardDriverDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO  loginRequestDTO,
                                                  HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String tokens[] = authService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

        Cookie cookie = new Cookie("token",  tokens[1]);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);

        return  ResponseEntity.ok(new LoginResponseDTO(tokens[0]));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponseDTO> refreshToken(HttpServletRequest httpServletRequest){

        String refresh = Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthorizationServiceException("Refresh token not found inside the cookie"));

        String accessToken = authService.refreshToken(refresh);

        return  ResponseEntity.ok(new LoginResponseDTO(accessToken));
    }

}
