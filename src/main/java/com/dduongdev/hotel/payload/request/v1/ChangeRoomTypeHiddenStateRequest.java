package com.dduongdev.hotel.payload.request.v1;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeRoomTypeHiddenStateRequest {

    @NotNull(message = "Hidden state is required")
    private boolean hidden;
}
