package com.dduongdev.hotel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
        User manager = new User();
        manager.setUsername(defaultManagerUsername);
        manager.setPassword(passwordEncoder.encode(defaultManagerPassword));
        manager.setRole(User.Role.MANAGER);
        userRepository.save(manager); 
    }
    
}
