package com.dduongdev.hotel.payload.request;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
