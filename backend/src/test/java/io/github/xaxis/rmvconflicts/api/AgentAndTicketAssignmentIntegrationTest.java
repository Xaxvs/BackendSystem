
package io.github.xaxis.rmvconflicts.api;

import com.jayway.jsonpath.JsonPath;
import io.github.xaxis.rmvconflicts.agent.Agent;
import io.github.xaxis.rmvconflicts.agent.AgentRepository;
import io.github.xaxis.rmvconflicts.tickets.Ticket;
import io.github.xaxis.rmvconflicts.tickets.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgentAndTicketAssignmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldCreateAndRetrieveAgent() throws Exception {
        MvcResult result = mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Support Agent",
                                  "email": "support@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.displayName").value("Support Agent"))
                .andExpect(jsonPath("$.email").value("support@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        long agentId = ((Number) JsonPath.read(
                result.getResponse().getContentAsString(), "$.id"
        )).longValue();

        // Flush changes to the database and clear the persistence context.
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/agents/{id}", agentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(agentId))
                .andExpect(jsonPath("$.displayName").value("Support Agent"))
                .andExpect(jsonPath("$.email").value("support@example.com"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRejectInvalidAgentRequest() throws Exception {
        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "",
                                  "email": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenAgentDoesNotExist() throws Exception {
        mockMvc.perform(get("/agents/{id}", -1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldEnforceCaseInsensitiveUniqueAgentEmail() {
        agentRepository.saveAndFlush(
                new Agent("First Agent", "duplicate@example.com")
        );

        assertThrows(DataIntegrityViolationException.class, () ->
                agentRepository.saveAndFlush(
                        new Agent("Second Agent", "DUPLICATE@example.com")
                )
        );
    }

    @Test
    void shouldAssignAgentToTicket() throws Exception {
        Agent agent = persistAgent("Support Agent", "assign@example.com");
        Ticket ticket = persistTicket();

        mockMvc.perform(put("/tickets/{id}/assignee", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "agentId": %d
                                }
                                """.formatted(agent.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.assigneeId").value(agent.getId()));

        // Ensure the assignment can be read back from the database.
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(agent.getId()));
    }

    @Test
    void shouldReturn404WhenAssigningNonexistentAgent() throws Exception {
        Ticket ticket = persistTicket();

        mockMvc.perform(put("/tickets/{id}/assignee", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "agentId": -1
                                }
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(nullValue()));
    }

    @Test
    void shouldReturn404WhenAssigningAgentToNonexistentTicket()
            throws Exception {
        Agent agent = persistAgent("Support Agent", "missing-ticket@example.com");

        mockMvc.perform(put("/tickets/{id}/assignee", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "agentId": %d
                                }
                                """.formatted(agent.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveTicketAssignee() throws Exception {
        Agent agent = persistAgent("Support Agent", "remove@example.com");
        Ticket ticket = persistTicket();
        ticket.setAssigneeId(agent.getId());
        ticketRepository.saveAndFlush(ticket);

        mockMvc.perform(delete("/tickets/{id}/assignee", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.assigneeId").value(nullValue()));

        // Verify the cleared assignment survives a database reload.
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/tickets/{id}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(nullValue()));
    }

    @Test
    void shouldReturn404WhenRemovingAssigneeFromNonexistentTicket()
            throws Exception {
        mockMvc.perform(delete("/tickets/{id}/assignee", -1L))
                .andExpect(status().isNotFound());
    }

    private Agent persistAgent(String displayName, String email) {
        return agentRepository.saveAndFlush(new Agent(displayName, email));
    }

    private Ticket persistTicket() {
        return ticketRepository.saveAndFlush(new Ticket(
                "Cannot reset password",
                "I never receive the password reset email.",
                "customer@example.com"
        ));
    }
}
