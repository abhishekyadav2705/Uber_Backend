package com.backend.abhishek.uber.services;

import com.backend.abhishek.uber.entities.User;
import com.backend.abhishek.uber.exceptions.ResourceNotFoundException;
import com.backend.abhishek.uber.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id "+id+" Not Found"));
    }
}
