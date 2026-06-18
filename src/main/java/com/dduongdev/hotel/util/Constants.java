package com.dduongdev.hotel.util;

import java.time.LocalTime;

public class Constants {
    public static final LocalTime CHECK_IN_TIME = LocalTime.of(14, 0);
    public static final LocalTime CHECK_OUT_TIME = LocalTime.of(12, 0);

    public static final String PHONE_NUMBER_PATTERN = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$";
    public static final String OTP_PATTERN = "^[0-9]{6}$";
}
