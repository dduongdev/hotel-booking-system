package com.dduongdev.hotel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.UsernameAlreadyExistsException;
import com.dduongdev.hotel.payload.request.v1.UserRegisterRequest;
import com.dduongdev.hotel.payload.response.v1.UserRegisterResponse;
import com.dduongdev.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists: " + username);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        userRepository.save(newUser);

        return new UserRegisterResponse(username);
    }
}
