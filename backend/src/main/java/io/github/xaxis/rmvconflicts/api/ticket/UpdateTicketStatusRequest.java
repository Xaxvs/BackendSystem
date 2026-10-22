package io.github.xaxis.rmvconflicts.api.ticket;

import jakarta.validation.constraints.NotNull;
import io.github.xaxis.rmvconflicts.tickets.TicketStatus;

public record UpdateTicketStatusRequest(@NotNull TicketStatus ticketStatus) {
}
