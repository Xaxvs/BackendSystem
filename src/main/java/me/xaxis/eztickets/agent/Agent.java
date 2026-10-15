package me.xaxis.eztickets.agent;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String display_name;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant created_at;

    protected Agent() {}

    public Agent(String display_name, String email) {
        this.display_name = display_name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

    public String getDisplayName() {
        return display_name;
    }
}
