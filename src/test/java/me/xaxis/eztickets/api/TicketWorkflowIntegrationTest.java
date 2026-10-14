
package me.xaxis.eztickets.api;

import me.xaxis.eztickets.tickets.Ticket;
import me.xaxis.eztickets.tickets.TicketManager;
import me.xaxis.eztickets.tickets.TicketRepository;
import me.xaxis.eztickets.tickets.TicketStatus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketManager ticketManager;

    @Autowired
    private TicketRepository ticketRepository;

    private final List<Long> createdTicketIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        ticketRepository.deleteAllById(createdTicketIds);
        createdTicketIds.clear();
    }

    private Ticket createTicket() {
        String unique = UUID.randomUUID().toString();

        Ticket ticket = ticketManager.createTicket(
                "Workflow test " + unique,
                "Ticket created by an integration test",
                "workflow-" + unique + "@example.com"
        );

        createdTicketIds.add(ticket.getId());
        return ticket;
    }

    private Ticket createTicketAtStatus(TicketStatus target)
            throws Exception {

        Ticket ticket = createTicket();
        long id = ticket.getId();

        switch (target) {
            case OPEN -> {
                // Tickets start in OPEN.
            }
            case IN_PROGRESS -> patchStatus(id, TicketStatus.IN_PROGRESS);
            case RESOLVED -> {
                patchStatus(id, TicketStatus.IN_PROGRESS);
                patchStatus(id, TicketStatus.RESOLVED);
            }
            case CLOSED -> {
                patchStatus(id, TicketStatus.IN_PROGRESS);
                patchStatus(id, TicketStatus.RESOLVED);
                patchStatus(id, TicketStatus.CLOSED);
            }
        }

        return ticket;
    }

    private void patchStatus(long id, TicketStatus target)
            throws Exception {

        mockMvc.perform(
                patch("/tickets/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"ticketStatus\":\"" + target.name() + "\"}"
                        )
        ).andExpect(status().isOk());
    }

    // --------------------------------------------------
    // Listing tickets
    // --------------------------------------------------

    @Test
    void listsTickets() throws Exception {
        Ticket ticket = createTicket();

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[*].id",
                                hasItem(ticket.getId().intValue())
                        )
                );
    }

    // --------------------------------------------------
    // Allowed status transitions
    // --------------------------------------------------

    static Stream<Arguments> validTransitions() {
        return Stream.of(
                Arguments.of(
                        TicketStatus.OPEN,
                        TicketStatus.IN_PROGRESS
                ),
                Arguments.of(
                        TicketStatus.IN_PROGRESS,
                        TicketStatus.OPEN
                ),
                Arguments.of(
                        TicketStatus.IN_PROGRESS,
                        TicketStatus.RESOLVED
                ),
                Arguments.of(
                        TicketStatus.RESOLVED,
                        TicketStatus.IN_PROGRESS
                ),
                Arguments.of(
                        TicketStatus.RESOLVED,
                        TicketStatus.CLOSED
                )
        );
    }

    @ParameterizedTest(name = "{0} -> {1} succeeds")
    @MethodSource("validTransitions")
    void allowsValidTransitions(
            TicketStatus from,
            TicketStatus to
    ) throws Exception {

        Ticket ticket = createTicketAtStatus(from);

        mockMvc.perform(
                        patch("/tickets/{id}/status", ticket.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"ticketStatus\":\"" + to.name() + "\"}"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(to.name()));

        // Verify the persisted state, not just the HTTP response.
        Ticket persisted = ticketRepository.findById(ticket.getId())
                .orElseThrow();

        assertEquals(to, persisted.getStatus());
    }

    // --------------------------------------------------
    // Rejected status transitions
    // --------------------------------------------------

    static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(TicketStatus.OPEN, TicketStatus.OPEN),
                Arguments.of(TicketStatus.OPEN, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.OPEN, TicketStatus.CLOSED),

                Arguments.of(
                        TicketStatus.IN_PROGRESS,
                        TicketStatus.IN_PROGRESS
                ),
                Arguments.of(
                        TicketStatus.IN_PROGRESS,
                        TicketStatus.CLOSED
                ),

                Arguments.of(TicketStatus.RESOLVED, TicketStatus.OPEN),
                Arguments.of(
                        TicketStatus.RESOLVED,
                        TicketStatus.RESOLVED
                ),

                Arguments.of(TicketStatus.CLOSED, TicketStatus.OPEN),
                Arguments.of(
                        TicketStatus.CLOSED,
                        TicketStatus.IN_PROGRESS
                ),
                Arguments.of(
                        TicketStatus.CLOSED,
                        TicketStatus.RESOLVED
                ),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.CLOSED)
        );
    }

    @ParameterizedTest(name = "{0} -> {1} is rejected")
    @MethodSource("invalidTransitions")
    void rejectsInvalidTransitions(
            TicketStatus from,
            TicketStatus to
    ) throws Exception {

        Ticket ticket = createTicketAtStatus(from);

        mockMvc.perform(
                patch("/tickets/{id}/status", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"ticketStatus\":\"" + to.name() + "\"}"
                        )
        ).andExpect(status().isBadRequest());

        // A rejected transition must not change the stored status.
        Ticket persisted = ticketRepository.findById(ticket.getId())
                .orElseThrow();

        assertEquals(from, persisted.getStatus());
    }

    // --------------------------------------------------
    // Missing tickets
    // --------------------------------------------------

    @Test
    void returnsNotFoundForMissingTicket() throws Exception {
        mockMvc.perform(
                patch("/tickets/{id}/status", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"ticketStatus\":\"IN_PROGRESS\"}"
                        )
        ).andExpect(status().isNotFound());
    }

    // --------------------------------------------------
    // Invalid request data
    // --------------------------------------------------

    @Test
    void rejectsNullTicketStatus() throws Exception {
        Ticket ticket = createTicket();

        mockMvc.perform(
                patch("/tickets/{id}/status", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ticketStatus\":null}")
        ).andExpect(status().isBadRequest());

        Ticket persisted = ticketRepository.findById(ticket.getId())
                .orElseThrow();

        assertEquals(TicketStatus.OPEN, persisted.getStatus());
    }

    @Test
    void rejectsUnknownTicketStatus() throws Exception {
        Ticket ticket = createTicket();

        mockMvc.perform(
                patch("/tickets/{id}/status", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ticketStatus\":\"WAITING\"}")
        ).andExpect(status().isBadRequest());

        Ticket persisted = ticketRepository.findById(ticket.getId())
                .orElseThrow();

        assertEquals(TicketStatus.OPEN, persisted.getStatus());
    }
}
