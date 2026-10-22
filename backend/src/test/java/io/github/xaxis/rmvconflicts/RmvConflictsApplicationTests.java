package io.github.xaxis.rmvconflicts;

import jakarta.transaction.Transactional;
import io.github.xaxis.rmvconflicts.api.ticket.TicketController;
import io.github.xaxis.rmvconflicts.tickets.Ticket;
import io.github.xaxis.rmvconflicts.tickets.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class RmvConflictsApplicationTests {

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	TicketController ticketController;

	@Test
	void contextLoads() {

		Ticket ticket = new Ticket("Test", "description", "email@email.com");
        ticketRepository.save(ticket);

        assertNotNull(ticket.getId());

		Optional<Ticket> test = ticketRepository.findById(ticket.getId());
		assertTrue(test.isPresent());

		Ticket t = test.orElseThrow();

		assertNotNull(t.getId());
		assertNotNull(t.getTitle());
		assertNotNull(t.getDescription());
		assertNotNull(t.getEmail());
		assertNotNull(t.getCreatedAt());
		assertNotNull(t.getStatus());

		assertEquals("Test", t.getTitle());
		assertEquals("description", t.getDescription());
		assertEquals("email@email.com", t.getEmail());
		assertEquals("OPEN", t.getStatus().name());

		System.out.printf(
				"ID: %s, Title: %s, Description: %s, Email: %s, Created at: %s, Ticket Status: %s",
				t.getId(), t.getTitle(), t.getDescription(),
				t.getEmail(), t.getCreatedAt(), t.getStatus()
		);

	}

}
