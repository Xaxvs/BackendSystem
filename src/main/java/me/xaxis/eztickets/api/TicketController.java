package me.xaxis.eztickets.api;

import jakarta.validation.Valid;
import me.xaxis.eztickets.tickets.Ticket;
import me.xaxis.eztickets.tickets.TicketManager;
import me.xaxis.eztickets.tickets.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketManager ticketManager;

    public TicketController(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getEmail(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
    }

    @GetMapping("/{id}")
    public TicketResponse getTicket(@PathVariable long id) {
        Optional<Ticket> optionalTicket = ticketManager.getTicketById(id);
        if (optionalTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return toTicketResponse(optionalTicket.get());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> setTicketStatus(@PathVariable long id, @Valid @RequestBody UpdateTicketStatusRequest request) {
        Ticket ticket = ticketManager.updateTicketStatus(id, request.ticketStatus());
        return ResponseEntity.status(HttpStatus.OK).body(toTicketResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<TicketPageResponse> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q
    ) {

        Page<Ticket> tickets = ticketManager.findTickets(page, size, direction, sortBy, status, q);

        TicketPageResponse request = new TicketPageResponse(
                tickets.getContent().stream().map(this::toTicketResponse).toList(),
                tickets.getNumber(),
                tickets.getSize(),
                tickets.getTotalPages(),
                tickets.getTotalElements(),
                tickets.isFirst(),
                tickets.isLast()
        );

        return ResponseEntity.ok(request);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        Ticket ticket = ticketManager.createTicket(
                ticketRequest.title(),
                ticketRequest.description(),
                ticketRequest.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toTicketResponse(ticket));
    }

}
