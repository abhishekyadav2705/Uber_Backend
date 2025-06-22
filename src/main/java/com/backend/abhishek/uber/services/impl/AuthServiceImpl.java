package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.OnboardDriverDTO;
import com.backend.abhishek.uber.dto.SignupDto;
import com.backend.abhishek.uber.dto.UserDto;
import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.enums.Role;
import com.backend.abhishek.uber.exceptions.ResourceNotFoundException;
import com.backend.abhishek.uber.exceptions.RuntimeConflictException;
import com.backend.abhishek.uber.repositories.UserRepository;
import com.backend.abhishek.uber.security.JWTService;
import com.backend.abhishek.uber.services.AuthService;
import com.backend.abhishek.uber.services.DriverService;
import com.backend.abhishek.uber.services.RiderService;
import com.backend.abhishek.uber.services.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailSenderServiceImpl emailSenderService;
    private final ObjectMapper objectMapper;


    @Override
    public String[] login(String email, String password) {
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));

        User user = (User) authentication.getPrincipal();

        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        String tokens[] = {accessToken,refreshToken};
        return tokens;
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {

        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if(user!=null){
            throw new RuntimeConflictException("User is  already registered with email Id : "+signupDto.getEmail());
        }

        User mapperUser = modelMapper.map(signupDto,User.class);
        mapperUser.setRoles(Set.of(Role.RIDER));
        mapperUser.setPassword(passwordEncoder.encode(mapperUser.getPassword()));
        User savedUser = userRepository.save(mapperUser);

        //create user related entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(OnboardDriverDTO onboardDriverDTO) {
        User user = userRepository.findById(onboardDriverDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + onboardDriverDTO.getUserId()));
        if(user.getRoles().contains(Role.DRIVER)){
            throw new RuntimeConflictException("User with id "+onboardDriverDTO.getUserId()+" is  already a driver");
        }

        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(onboardDriverDTO.getVehicleId()
                )
                .available(true)
                .build();
        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverService.createNewDriver(createDriver);

        return modelMapper.map(savedDriver,DriverDto.class);
    }

    @Override
    public String refreshToken(String refresh) {
        Long userId = jwtService.getUserIdFromToken(refresh);
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User nt found with id "+userId));

        return jwtService.generateAccessToken(user);
    }

    @Override
    public void initiateSignup(SignupDto signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new RuntimeConflictException("Email already registered: " + signupDto.getEmail());
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        String email = signupDto.getEmail();
        String otpKey = "SignupOTP:" + email;
        String dataKey = "SignupData:" + email;

        try {
            String signupJson = objectMapper.writeValueAsString(signupDto);
            stringRedisTemplate.opsForValue().set(otpKey, otp, 5, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(dataKey, signupJson, 5, TimeUnit.MINUTES);

            String htmlBody = buildOtpEmailHtml(otp);
            emailSenderService.sendSignUpOtpEmail(email, htmlBody); // send HTML content
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process signup data", e);
        }
    }

    @Override
    @Transactional
    public UserDto verifySignupOtp(String email, String inputOtp) throws JsonProcessingException {
        String otpKey = "SignupOTP:" + email;
        String dataKey = "SignupData:" + email;

        String storedOtp = stringRedisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null || !storedOtp.equals(inputOtp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        String json = stringRedisTemplate.opsForValue().get(dataKey);
        if (json == null) {
            throw new RuntimeException("Signup data expired.");
        }

        SignupDto signupDto = new ObjectMapper().readValue(json, SignupDto.class);

        // Proceed with your existing logic
        User mapperUser = modelMapper.map(signupDto, User.class);
        mapperUser.setRoles(Set.of(Role.RIDER));
        mapperUser.setPassword(passwordEncoder.encode(mapperUser.getPassword()));

        User savedUser = userRepository.save(mapperUser);
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        // Clean up Redis
        stringRedisTemplate.delete(Arrays.asList(otpKey, dataKey));

        return modelMapper.map(savedUser, UserDto.class);
    }

    private String buildOtpEmailHtml(String otp) {
        return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <style>
    body {
      margin: 0;
      padding: 0;
      background: #f4f6f8;
      font-family: 'Segoe UI', sans-serif;
    }
    .container {
      max-width: 520px;
      margin: 40px auto;
      background: #ffffff;
      padding: 30px 40px;
      border-radius: 12px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }
    .logo {
      text-align: center;
      margin-bottom: 25px;
    }
    .logo img {
      max-width: 120px;
    }
    .heading {
      text-align: center;
      color: #222;
      font-size: 22px;
      margin-bottom: 10px;
    }
    .description {
      text-align: center;
      color: #444;
      font-size: 14px;
      margin-bottom: 30px;
    }
    .otp-box {
      text-align: center;
      font-size: 30px;
      background: #1e3a8a; /* dark blue */
      color: #ffffff;
      padding: 18px 0;
      border-radius: 10px;
      letter-spacing: 6px;
      font-weight: bold;
      margin: 0 auto 30px auto;
      width: 70%%;
      border: 2px solid #1e40af;
      box-shadow: 0 0 6px rgba(0,0,0,0.15);
    }
    .note {
      text-align: center;
      color: #777;
      font-size: 13px;
    }
    .footer {
      text-align: center;
      font-size: 12px;
      color: #999;
      margin-top: 40px;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="logo">
      <img src="https://upload.wikimedia.org/wikipedia/commons/c/cc/Uber_logo_2018.png" alt="Uber-App Logo">
    </div>
    <div class="heading">Verify Your Email Address</div>
    <div class="description">
      Thanks for signing up with <strong>Uber-App</strong>!<br>
      Enter the OTP below to complete your signup. It is valid for <strong>5 minutes</strong>.
    </div>
    <div class="otp-box">%s</div>
    <div class="note">
      Didn’t request this? Just ignore this email.
    </div>
    <div class="footer">
      &copy; 2025 Uber-App. All rights reserved.
    </div>
  </div>
</body>
</html>
""".formatted(otp);
    }


}
