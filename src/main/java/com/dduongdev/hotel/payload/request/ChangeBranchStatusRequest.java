package com.dduongdev.hotel.payload.request;

import com.dduongdev.hotel.entity.Branch.Status;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeBranchStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;
}
