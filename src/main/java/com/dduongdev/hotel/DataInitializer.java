package com.dduongdev.hotel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Value("${default.manager_username}")
    private String defaultManagerUsername;

    @Value("${default.manager_password}")
    private String defaultManagerPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername(defaultManagerUsername)) {
            User manager = new User();
            manager.setUsername(defaultManagerUsername);
            manager.setPassword(passwordEncoder.encode(defaultManagerPassword));
            manager.setPhoneNumber("0000000000");
            manager.setRole(User.Role.MANAGER);
            manager.setPhoneVerified(true);
            userRepository.save(manager);
            log.info("Default manager account created: {}", defaultManagerUsername);
        } else {
            log.info("Default manager account already exists: {}", defaultManagerUsername);
        }
    }

}
