package io.github.xaxis.rmvconflicts.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank String displayName,
    @Email @NotBlank String email

) {
}
