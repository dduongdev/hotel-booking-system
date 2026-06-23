package com.dduongdev.hotel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.InvalidOtpException;
import com.dduongdev.hotel.exception.PhoneNumberAlreadyExistsException;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.exception.UsernameAlreadyExistsException;
import com.dduongdev.hotel.payload.request.UserRegisterRequest;
import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.security.entity.HotelUserDetails;
import com.dduongdev.hotel.security.payload.response.LoginResponse;
import com.dduongdev.hotel.security.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final AuthService authService;

    @Transactional
    public LoginResponse register(UserRegisterRequest request) {
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

        HotelUserDetails userDetails = new HotelUserDetails(newUser.getId(), newUser.getUsername(), null,
                newUser.getRole(), newUser.isPhoneVerified());
        return authService.generateTokenPair(userDetails);
    }

    @Transactional
    public LoginResponse activateUserByOtp(Long userId, String otpCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean verified = otpService.verifyOtp(user.getPhoneNumber(), otpCode);

        if (!verified) {
            throw new InvalidOtpException();
        }

        user.setPhoneVerified(true);
        userRepository.save(user);

        HotelUserDetails userDetails = new HotelUserDetails(user.getId(), user.getUsername(), null,
                user.getRole(), user.isPhoneVerified());
        return authService.generateTokenPair(userDetails);
    }

    public void sendOtpForActivation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        otpService.sendOtp(user.getPhoneNumber());
    }
}
