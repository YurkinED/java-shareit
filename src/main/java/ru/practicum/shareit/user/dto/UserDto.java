package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    long id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
}
