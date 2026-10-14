package me.xaxis.eztickets.api.ticket;

import jakarta.validation.constraints.NotNull;
import me.xaxis.eztickets.tickets.TicketStatus;

public record UpdateTicketStatusRequest(@NotNull TicketStatus ticketStatus) {
}
