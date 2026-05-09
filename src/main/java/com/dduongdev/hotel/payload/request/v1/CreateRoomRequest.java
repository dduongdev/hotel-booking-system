package com.dduongdev.hotel.payload.request.v1;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRoomRequest {

    @NotNull(message = "Room name is required")
    @NotEmpty(message = "Room name cannot be empty")
    @NotBlank(message = "Room name cannot be blank")
    private String name;

    @NotNull(message = "Room type ID is required")
    private int roomTypeId;
}
