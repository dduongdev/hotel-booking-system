package com.dduongdev.hotel.security.config;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dduongdev.hotel.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/refresh-token").permitAll()
                .requestMatchers("/api/v1/users/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches/*/room-types").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches/*/room-types/availabilities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches/*/rooms").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/branches/*/rooms/available").permitAll()

                .requestMatchers("/error").permitAll()
                .requestMatchers("/", "/index.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/uploads/**").permitAll()
                .requestMatchers("/login", "/register", "/auth/change-password", "/verify-phone").permitAll()
                .requestMatchers("/room-types/**").permitAll()
                .requestMatchers("/rooms/**").permitAll()
                .requestMatchers("/admin/**").permitAll()
                .requestMatchers("/bookings/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:8080"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public Base64.Encoder base64Encoder() {
        return Base64.getUrlEncoder().withoutPadding();
    }
}
