package io.github.xaxis.rmvconflicts.api.agent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentRequest(

        @NotBlank
        @Size(max = 40, min = 2)
        String displayName,

        @Size(max = 254, min = 1)
        @Email
        @NotBlank
        String email

) {
}
