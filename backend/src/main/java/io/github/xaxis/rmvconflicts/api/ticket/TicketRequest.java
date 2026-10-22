package io.github.xaxis.rmvconflicts.api.ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketRequest(

        @NotBlank
        @Size(max = 200, min = 1)
        String title,

        @NotBlank
        String description,

        @Size(max = 254, min = 1)
        @Email
        @NotBlank
        String email
) {
}
