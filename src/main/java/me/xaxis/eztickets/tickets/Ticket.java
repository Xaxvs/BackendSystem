package me.xaxis.eztickets.tickets;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private TicketStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "assignee_id")
    private Long assigneeId;

    protected Ticket() {
    }

    public Ticket(
            String title,
            String description,
            String email
    ) {
        this.title = title;
        this.description = description;
        this.email = email;
        this.status = TicketStatus.OPEN;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setInProgress() {
        status = TicketStatus.IN_PROGRESS;
    }

    public void resolve() {
        status = TicketStatus.RESOLVED;
    }

    public void close() {
        status = TicketStatus.CLOSED;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void open() {
        status = TicketStatus.OPEN;
    }
}
