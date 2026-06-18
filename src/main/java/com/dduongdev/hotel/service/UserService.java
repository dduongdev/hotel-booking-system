package com.dduongdev.hotel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.PhoneNumberAlreadyExistsException;
import com.dduongdev.hotel.exception.UsernameAlreadyExistsException;
import com.dduongdev.hotel.payload.request.UserRegisterRequest;
import com.dduongdev.hotel.payload.response.UserRegisterResponse;
import com.dduongdev.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String phoneNumber = request.getPhoneNumber();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setPhoneNumber(phoneNumber);

        userRepository.save(newUser);
        
        otpService.sendOtp(phoneNumber);

        return new UserRegisterResponse(username);
    }
}
