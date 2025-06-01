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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

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
}
