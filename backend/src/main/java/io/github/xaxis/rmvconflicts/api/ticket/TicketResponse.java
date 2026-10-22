package io.github.xaxis.rmvconflicts.api.ticket;

import io.github.xaxis.rmvconflicts.tickets.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        String title,
        String description,
        String email,
        TicketStatus status,
        Instant createdAt,
        Long assigneeId
) {
}
