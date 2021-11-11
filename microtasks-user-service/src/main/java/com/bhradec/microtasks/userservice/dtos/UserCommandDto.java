package com.bhradec.microtasks.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommandDto {
    @NotNull(message = "First name must not be null")
    @NotBlank(message = "First name must not be blank or empty")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @NotBlank(message = "Last name must not be blank or empty")
    private String lastName;

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank or empty")
    private String username;

    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank or empty")
    @Email(message = "Email must be a valid email adress")
    private String email;

    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank or empty")
    private String password;

    @NotNull(message = "Team id must not be null")
    private Long teamId;
}
