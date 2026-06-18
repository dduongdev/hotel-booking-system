package com.dduongdev.hotel.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.security.entity.HotelUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new HotelUserDetails(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole(),
                        user.isPhoneVerified()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
}
