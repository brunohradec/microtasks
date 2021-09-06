package com.bhradec.microtasks.teamservice.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCommand {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank or empty")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank or empty")
    private String description;
}
