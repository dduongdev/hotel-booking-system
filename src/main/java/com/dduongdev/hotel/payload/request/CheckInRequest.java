package com.dduongdev.hotel.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CheckInRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
