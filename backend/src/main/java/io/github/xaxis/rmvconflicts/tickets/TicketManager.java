package io.github.xaxis.rmvconflicts.tickets;

import jakarta.validation.constraints.NotNull;
import io.github.xaxis.rmvconflicts.agent.Agent;
import io.github.xaxis.rmvconflicts.agent.AgentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Service
public class TicketManager {

    private final TicketRepository ticketRepository;
    private final AgentRepository agentRepository;

    public TicketManager(TicketRepository ticketRepository, AgentRepository agentRepository) {
        this.ticketRepository = ticketRepository;
        this.agentRepository = agentRepository;
    }

    public Ticket createTicket(String title, String description, String email) {
        return ticketRepository.save(new Ticket(title, description, email));
    }

    private Ticket getTicket(long id) {
        Optional<Ticket> optionalTicket = getTicketById(id);
        if (optionalTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return optionalTicket.get();
    }

    @Transactional
    public Ticket updateTicketStatus(long id, TicketStatus to) {
        Ticket ticket = getTicket(id);
        TicketStatus status = ticket.getStatus();

        switch (to) {
            case OPEN -> {
                if (status != TicketStatus.IN_PROGRESS)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot set ticket status to open");
                ticket.open();
            }
            case IN_PROGRESS -> {
                if (status == TicketStatus.OPEN || status == TicketStatus.RESOLVED) {
                    ticket.setInProgress();
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot set ticket status to in progress");
            }
            case RESOLVED -> {
                if (status != TicketStatus.IN_PROGRESS)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot resolve ticket");
                ticket.resolve();
            }
            case CLOSED -> {
                if (status != TicketStatus.RESOLVED)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot close ticket");
                ticket.close();
            }
            case null, default ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket status doesn't exist");
        }
        return ticket;
    }

    public Pageable createTicketPageable(int page, int size, String direction, String sortBy) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to process tickets request");
        }
        if (Arrays.stream(TicketSortCases.values()).noneMatch(ticketSortCases -> ticketSortCases.getString().equals(sortBy))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad sortBy tickets request");
        }
        if (direction == null || Arrays.stream(Sort.Direction.values()).noneMatch(dirEnum -> dirEnum.name().toLowerCase(Locale.ROOT).equals(direction))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad direction tickets request");
        }
        return PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.fromString(direction),
                        sortBy,
                        "id"
                )
        );
    }

    public Optional<Ticket> getTicketById(long id) {
        return ticketRepository.findById(id);
    }

    public Page<Ticket> findTickets(int page, int size, String direction, String sortBy, TicketStatus ticketStatus, String q) {
        return ticketRepository.findAll(
                TicketSpecifications.hasStatusAndText(ticketStatus, q),
                createTicketPageable(page, size, direction, sortBy)
        );
    }

    @Transactional
    public Ticket updateTicketAssignee(long ticketId, @NotNull Long agentId) {
        Ticket ticket = getTicket(ticketId);
        Optional<Agent> agent = agentRepository.findById(agentId);
        if(agent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found");
        }
        ticket.setAssigneeId(agentId);
        return ticket;
    }

    @Transactional
    public Ticket removeTicketAssignee(long id) {
        Ticket ticket = getTicket(id);
        ticket.setAssigneeId(null);
        return ticket;
    }
}
