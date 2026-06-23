package com.dduongdev.hotel.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateRoomRequest {

    @NotNull(message = "Room name is required")
    @NotEmpty(message = "Room name cannot be empty")
    @NotBlank(message = "Room name cannot be blank")
    private String name;

    private Boolean hidden;

    @NotNull(message = "Room type ID is required")
    private Long roomTypeId;
}
