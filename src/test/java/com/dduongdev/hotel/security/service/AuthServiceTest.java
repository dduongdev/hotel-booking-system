package com.dduongdev.hotel.security.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dduongdev.hotel.entity.User;
import com.dduongdev.hotel.exception.InvalidCurrentPasswordException;
import com.dduongdev.hotel.exception.PasswordConfirmationMismatchException;
import com.dduongdev.hotel.exception.ResourceNotFoundException;
import com.dduongdev.hotel.payload.request.ChangePasswordRequest;
import com.dduongdev.hotel.repository.UserRepository;
import com.dduongdev.hotel.security.entity.RefreshToken;
import com.dduongdev.hotel.security.payload.response.LoginResponse;
import com.dduongdev.hotel.security.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final Long USER_ID = 1L;
    private static final String CURRENT_PASSWORD = "oldPwd123";
    private static final String NEW_PASSWORD = "newPwd456";
    private static final String ENCODED_NEW_PASSWORD = "encodedNewPwd";
    private static final String ACCESS_TOKEN = "access-token-xyz";
    private static final String REFRESH_TOKEN_VALUE = "refresh-token-abc";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername("testuser");
        user.setPassword("encodedOldPwd");
        user.setRole(User.Role.CUSTOMER);
    }

    private ChangePasswordRequest mockRequest(String currentPassword, String newPassword, String confirmNewPassword) {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);
        lenient().when(request.getCurrentPassword()).thenReturn(currentPassword);
        lenient().when(request.getNewPassword()).thenReturn(newPassword);
        lenient().when(request.getConfirmNewPassword()).thenReturn(confirmNewPassword);
        return request;
    }

    // ──────────────────────────────────────────────
    // Test 1: Should throw ResourceNotFoundException
    // ──────────────────────────────────────────────
    @Test
    void changePassword_shouldThrowResourceNotFoundException_whenUserNotFound() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> authService.changePassword(USER_ID, request),
                "Should throw ResourceNotFoundException when user does not exist");
    }

    // ─────────────────────────────────────────────────────
    // Test 2: Should throw InvalidCurrentPasswordException
    // ─────────────────────────────────────────────────────
    @Test
    void changePassword_shouldThrowInvalidCurrentPasswordException_whenCurrentPasswordDoesNotMatch() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(passwordEncoder.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(InvalidCurrentPasswordException.class,
                () -> authService.changePassword(USER_ID, request),
                "Should throw InvalidCurrentPasswordException when current password is wrong");
    }

    // ──────────────────────────────────────────────────────────
    // Test 3: Should throw PasswordConfirmationMismatchException
    // ──────────────────────────────────────────────────────────
    @Test
    void changePassword_shouldThrowPasswordConfirmationMismatchException_whenNewPasswordsDoNotMatch() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, "differentConfirmPassword");
        when(passwordEncoder.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);

        // when & then
        assertThrows(PasswordConfirmationMismatchException.class,
                () -> authService.changePassword(USER_ID, request),
                "Should throw PasswordConfirmationMismatchException when new passwords do not match");
    }

    // ─────────────────────────────────────────────
    // Test 4: Verify old refresh tokens are deleted
    // ─────────────────────────────────────────────
    @Test
    void changePassword_shouldDeleteOldRefreshTokens() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(passwordEncoder.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(jwtService.generateToken(any())).thenReturn(ACCESS_TOKEN);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(REFRESH_TOKEN_VALUE);
        refreshToken.setExpirationTime(LocalDateTime.now().plusDays(7));
        when(refreshTokenService.create(USER_ID)).thenReturn(refreshToken);

        // when
        authService.changePassword(USER_ID, request);

        // then
        verify(refreshTokenRepository).deleteByUserId(USER_ID);
    }

    // ────────────────────────────────────────────────
    // Test 5: Verify new password is saved for the user
    // ────────────────────────────────────────────────
    @Test
    void changePassword_shouldSaveNewPasswordForUser() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(passwordEncoder.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(jwtService.generateToken(any())).thenReturn(ACCESS_TOKEN);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(REFRESH_TOKEN_VALUE);
        refreshToken.setExpirationTime(LocalDateTime.now().plusDays(7));
        when(refreshTokenService.create(USER_ID)).thenReturn(refreshToken);

        // when
        authService.changePassword(USER_ID, request);

        // then
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_NEW_PASSWORD, savedUser.getPassword(),
                "The saved user should have the encoded new password");
    }

    // ────────────────────────────────────────────────
    // Test 6: Verify new refresh token is saved
    // ────────────────────────────────────────────────
    @Test
    void changePassword_shouldCreateNewRefreshToken() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangePasswordRequest request = mockRequest(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(passwordEncoder.matches(CURRENT_PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(jwtService.generateToken(any())).thenReturn(ACCESS_TOKEN);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(REFRESH_TOKEN_VALUE);
        refreshToken.setExpirationTime(LocalDateTime.now().plusDays(7));
        when(refreshTokenService.create(USER_ID)).thenReturn(refreshToken);

        // when
        LoginResponse response = authService.changePassword(USER_ID, request);

        // then
        verify(refreshTokenService).create(USER_ID);
        assertNotNull(response, "LoginResponse should not be null");
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN_VALUE, response.getRefreshToken());
    }
}
