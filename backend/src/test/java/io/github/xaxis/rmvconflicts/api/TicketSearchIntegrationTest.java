
package io.github.xaxis.rmvconflicts.api;

import io.github.xaxis.rmvconflicts.tickets.Ticket;
import io.github.xaxis.rmvconflicts.tickets.TicketManager;
import io.github.xaxis.rmvconflicts.tickets.TicketRepository;
import io.github.xaxis.rmvconflicts.tickets.TicketStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketManager ticketManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<Long> createdTicketIds = new HashSet<>();

    @AfterEach
    void cleanUpCreatedTickets() {
        ticketRepository.deleteAllById(createdTicketIds);
        createdTicketIds.clear();
    }

    // ---------- Default pagination and sorting ----------

    @Test
    void defaultsToPageZeroSizeTwentyAndCreatedAtDescending() throws Exception {
        createTicket("Default sort A", "Description A");
        createTicket("Default sort B", "Description B");
        createTicket("Default sort C", "Description C");

        // Read the persisted data to calculate the expected database order.
        List<Ticket> allTickets = ticketRepository.findAll();

        List<Long> expectedIds = allTickets.stream()
                .sorted(
                        Comparator.comparing(Ticket::getCreatedAt)
                                .thenComparing(Ticket::getId)
                                .reversed()
                )
                .limit(20)
                .map(Ticket::getId)
                .toList();

        int totalElements = allTickets.size();
        int totalPages = (int) Math.ceil(totalElements / 20.0);

        MvcResult result = mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(totalElements))
                .andExpect(jsonPath("$.totalPages").value(totalPages))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(totalPages <= 1))
                .andReturn();

        assertEquals(expectedIds, idsFrom(result));
    }

    // ---------- Pagination boundaries ----------

    @Test
    void paginatesWithoutDuplicatesAndReturnsCorrectMetadata() throws Exception {
        String marker = uniqueMarker();

        List<Ticket> created = List.of(
                createTicket("Page " + marker + " A", "Description A"),
                createTicket("Page " + marker + " B", "Description B"),
                createTicket("Page " + marker + " C", "Description C"),
                createTicket("Page " + marker + " D", "Description D"),
                createTicket("Page " + marker + " E", "Description E")
        );

        List<Long> expectedIds = created.stream()
                .sorted(Comparator.comparing(Ticket::getTitle))
                .map(Ticket::getId)
                .toList();

        MvcResult page0 = mockMvc.perform(get("/tickets")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andReturn();

        MvcResult page1 = mockMvc.perform(get("/tickets")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false))
                .andReturn();

        MvcResult page2 = mockMvc.perform(get("/tickets")
                        .param("page", "2")
                        .param("size", "2")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andReturn();

        List<Long> allPageIds = new ArrayList<>();
        allPageIds.addAll(idsFrom(page0));
        allPageIds.addAll(idsFrom(page1));
        allPageIds.addAll(idsFrom(page2));

        assertEquals(expectedIds, allPageIds);
        assertEquals(5, new HashSet<>(allPageIds).size());
    }

    @Test
    void usesIdAsTieBreakerWhenPrimarySortValuesAreEqual() throws Exception {
        String marker = uniqueMarker();
        String sharedTitle = "Same title " + marker;

        Ticket first = createTicket(sharedTitle, "First " + marker);
        Ticket second = createTicket(sharedTitle, "Second " + marker);
        Ticket third = createTicket(sharedTitle, "Third " + marker);

        List<Long> expectedIds = List.of(
                first.getId(),
                second.getId(),
                third.getId()
        ).stream().sorted().toList();

        MvcResult page0 = mockMvc.perform(get("/tickets")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult page1 = mockMvc.perform(get("/tickets")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andReturn();

        List<Long> actualIds = new ArrayList<>();
        actualIds.addAll(idsFrom(page0));
        actualIds.addAll(idsFrom(page1));

        assertEquals(expectedIds, actualIds);
    }

    @Test
    void acceptsMinimumAndMaximumPageSizes() throws Exception {
        String marker = uniqueMarker();

        createTicket("Size test A " + marker, "Description A");
        createTicket("Size test B " + marker, "Description B");

        mockMvc.perform(get("/tickets")
                        .param("size", "1")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get("/tickets")
                        .param("size", "100")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void rejectsNegativePageNumber() throws Exception {
        mockMvc.perform(get("/tickets").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 101})
    void rejectsInvalidPageSizes(int size) throws Exception {
        mockMvc.perform(get("/tickets").param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    // ---------- Status filtering ----------

    @Test
    void filtersTicketsByEverySupportedStatus() throws Exception {
        String marker = uniqueMarker();
        Map<TicketStatus, Ticket> ticketsByStatus =
                new EnumMap<>(TicketStatus.class);

        for (TicketStatus targetStatus : TicketStatus.values()) {
            Ticket ticket = createTicket(
                    "Status " + marker + " " + targetStatus.name(),
                    "Description " + marker
            );

            moveToStatus(ticket, targetStatus);
            ticketsByStatus.put(targetStatus, ticket);
        }

        for (TicketStatus targetStatus : TicketStatus.values()) {
            MvcResult result = mockMvc.perform(get("/tickets")
                            .param("status", targetStatus.name())
                            .param("q", marker))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andReturn();

            assertEquals(
                    List.of(ticketsByStatus.get(targetStatus).getId()),
                    idsFrom(result)
            );
        }
    }

    @Test
    void rejectsUnknownStatus() throws Exception {
        mockMvc.perform(get("/tickets").param("status", "WAITING"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Text search ----------

    @Test
    void searchesCaseInsensitivelyAndMatchesPartialTextInTitleOrDescription()
            throws Exception {
        String marker = uniqueMarker();
        String partialSearch = marker
                .substring(2, marker.length() - 2)
                .toUpperCase(Locale.ROOT);

        Ticket titleMatch = createTicket(
                "Prefix " + marker.toLowerCase(Locale.ROOT) + " suffix",
                "Ordinary description"
        );

        Ticket descriptionMatch = createTicket(
                "Login failure",
                "Could not connect to " + marker.toLowerCase(Locale.ROOT)
        );

        Ticket noMatch = createTicket(
                "Billing issue",
                "Payment was declined"
        );

        MvcResult result = mockMvc.perform(get("/tickets")
                        .param("q", partialSearch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andReturn();

        List<Long> actualIds = idsFrom(result);

        assertTrue(actualIds.contains(titleMatch.getId()));
        assertTrue(actualIds.contains(descriptionMatch.getId()));
        assertFalse(actualIds.contains(noMatch.getId()));
    }

    @Test
    void combinesTextSearchAndStatusFilters() throws Exception {
        String marker = uniqueMarker();

        Ticket openMatch = createTicket(
                "Open issue " + marker,
                "Description"
        );

        Ticket inProgressMatch = createTicket(
                "Another issue",
                "Details " + marker
        );
        moveToStatus(inProgressMatch, TicketStatus.IN_PROGRESS);

        createTicket("Unrelated open issue", "No matching text");

        MvcResult result = mockMvc.perform(get("/tickets")
                        .param("status", "OPEN")
                        .param("q", marker.toUpperCase(Locale.ROOT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andReturn();

        assertEquals(List.of(openMatch.getId()), idsFrom(result));
    }

    @Test
    void blankSearchHasTheSameResultsAsNoSearch() throws Exception {
        createTicket("Blank search A", "Description A");
        createTicket("Blank search B", "Description B");

        MvcResult withoutSearch = mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult withBlankSearch = mockMvc.perform(get("/tickets")
                        .param("q", "   "))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(idsFrom(withoutSearch), idsFrom(withBlankSearch));

        JsonNode normalResponse = objectMapper.readTree(
                withoutSearch.getResponse().getContentAsString()
        );
        JsonNode blankResponse = objectMapper.readTree(
                withBlankSearch.getResponse().getContentAsString()
        );

        assertEquals(
                normalResponse.get("totalElements"),
                blankResponse.get("totalElements")
        );
    }

    @Test
    void returnsEmptyPageWhenNothingMatches() throws Exception {
        MvcResult result = mockMvc.perform(get("/tickets")
                        .param("q", uniqueMarker()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andReturn();

        assertTrue(idsFrom(result).isEmpty());
    }

    // ---------- Sorting ----------

    @Test
    void sortsByTitleAscendingAndDescending() throws Exception {
        String marker = uniqueMarker();

        List<Ticket> created = List.of(
                createTicket("Charlie " + marker, "Description"),
                createTicket("Alpha " + marker, "Description"),
                createTicket("Bravo " + marker, "Description")
        );

        List<Long> expectedAscending = created.stream()
                .sorted(Comparator.comparing(Ticket::getTitle))
                .map(Ticket::getId)
                .toList();

        List<Long> expectedDescending = created.stream()
                .sorted(Comparator.comparing(Ticket::getTitle).reversed())
                .map(Ticket::getId)
                .toList();

        MvcResult ascending = mockMvc.perform(get("/tickets")
                        .param("sortBy", "title")
                        .param("direction", "asc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult descending = mockMvc.perform(get("/tickets")
                        .param("sortBy", "title")
                        .param("direction", "desc")
                        .param("q", marker))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedAscending, idsFrom(ascending));
        assertEquals(expectedDescending, idsFrom(descending));
    }

    @Test
    void rejectsUnsupportedSortField() throws Exception {
        mockMvc.perform(get("/tickets").param("sortBy", "notARealField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnsupportedSortDirection() throws Exception {
        mockMvc.perform(get("/tickets").param("direction", "sideways"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Helpers ----------

    private Ticket createTicket(String title, String description) {
        String email = "test+" + UUID.randomUUID() + "@example.com";

        Ticket ticket = ticketManager.createTicket(title, description, email);
        createdTicketIds.add(ticket.getId());

        return ticket;
    }

    private void moveToStatus(Ticket ticket, TicketStatus targetStatus) {
        if (targetStatus == TicketStatus.OPEN) {
            return;
        }

        ticketManager.updateTicketStatus(
                ticket.getId(),
                TicketStatus.IN_PROGRESS
        );

        if (targetStatus == TicketStatus.IN_PROGRESS) {
            return;
        }

        ticketManager.updateTicketStatus(
                ticket.getId(),
                TicketStatus.RESOLVED
        );

        if (targetStatus == TicketStatus.RESOLVED) {
            return;
        }

        ticketManager.updateTicketStatus(
                ticket.getId(),
                TicketStatus.CLOSED
        );
    }

    private List<Long> idsFrom(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        JsonNode content = root.get("content");

        assertNotNull(
                content,
                "Expected the page-response list property to be named 'content'."
        );
        assertTrue(content.isArray());

        List<Long> ids = new ArrayList<>();

        for (JsonNode ticket : content) {
            ids.add(ticket.path("id").asLong());
        }

        return ids;
    }

    private String uniqueMarker() {
        return "TestMarker" + UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
