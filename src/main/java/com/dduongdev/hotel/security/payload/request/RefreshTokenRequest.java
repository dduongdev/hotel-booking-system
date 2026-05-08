package com.dduongdev.hotel.security.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

    @NotNull(message = "Refresh token is required")
    @NotEmpty(message = "Refresh token cannot be empty")
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}
