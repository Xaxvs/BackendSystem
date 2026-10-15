package me.xaxis.eztickets.api.ticket;

import me.xaxis.eztickets.tickets.TicketStatus;

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
