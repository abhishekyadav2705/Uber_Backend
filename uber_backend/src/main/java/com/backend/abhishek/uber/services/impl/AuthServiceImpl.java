package com.backend.abhishek.uber.services.impl;

import com.backend.abhishek.uber.dto.DriverDto;
import com.backend.abhishek.uber.dto.SignupDto;
import com.backend.abhishek.uber.dto.UserDto;
import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.entities.enums.Role;
import com.backend.abhishek.uber.exceptions.RuntimeConflictException;
import com.backend.abhishek.uber.repositories.UserRepository;
import com.backend.abhishek.uber.services.AuthService;
import com.backend.abhishek.uber.services.RiderService;
import com.backend.abhishek.uber.services.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;

    @Override
    public String login(String email, String password) {
        return "";
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
        User savedUser = userRepository.save(mapperUser);

        //create user related entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
