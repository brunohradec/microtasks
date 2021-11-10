package com.bhradec.microtasks.taskservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommandDto {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Start time must not be null")
    private Date startTime;

    @NotNull(message = "Deadline must not be null")
    private Date deadline;

    @NotNull(message = "User id must not be null")
    private Long userId;
}
