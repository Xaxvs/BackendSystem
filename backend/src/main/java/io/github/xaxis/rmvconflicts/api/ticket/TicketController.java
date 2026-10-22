package io.github.xaxis.rmvconflicts.api.ticket;

import jakarta.validation.Valid;
import io.github.xaxis.rmvconflicts.api.PageResponse;
import io.github.xaxis.rmvconflicts.tickets.Ticket;
import io.github.xaxis.rmvconflicts.tickets.TicketManager;
import io.github.xaxis.rmvconflicts.tickets.TicketStatus;
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
                ticket.getCreatedAt(),
                ticket.getAssigneeId()
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

    @DeleteMapping("/{id}/assignee")
    public ResponseEntity<TicketResponse> removeTicketAssignee(@PathVariable long id) {
        Ticket ticket = ticketManager.removeTicketAssignee(id);
        return ResponseEntity.status(HttpStatus.OK).body(toTicketResponse(ticket));
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<TicketResponse> setTicketAssignee(@PathVariable long id, @Valid @RequestBody AssignAgentRequest request){
        Ticket ticket = ticketManager.updateTicketAssignee(id, request.agentId());
        return ResponseEntity.status(HttpStatus.OK).body(toTicketResponse(ticket));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> setTicketStatus(@PathVariable long id, @Valid @RequestBody UpdateTicketStatusRequest request) {
        Ticket ticket = ticketManager.updateTicketStatus(id, request.ticketStatus());
        return ResponseEntity.status(HttpStatus.OK).body(toTicketResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TicketResponse>> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q
    ) {

        Page<Ticket> tickets = ticketManager.findTickets(page, size, direction, sortBy, status, q);

        PageResponse<TicketResponse> request = new PageResponse<>(
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
