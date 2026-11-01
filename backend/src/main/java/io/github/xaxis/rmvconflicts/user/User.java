package io.github.xaxis.rmvconflicts.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "created_at")
  private Instant createdAt;

  @Column(nullable = false, unique = true, name = "username")
  private String username;

  @Column(nullable = false)
  private String email;

  @Column(name = "last_activity_at")
  private Instant lastActivityAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  protected User() {
  }

  public User(String username, String email) {
    this.username = username.toLowerCase(Locale.ROOT);
    this.email = email.toLowerCase(Locale.ROOT);
    this.status = UserStatus.IDLE;
  }

  public void setActive() {
    status = UserStatus.CLAIM_ACTIVE;
  }

  public void setIdle() {
    status = UserStatus.IDLE;
  }

  public Long getId() { return id; }
  public Instant getCreatedAt() { return createdAt; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public Instant getLastActivityAt() { return lastActivityAt; }
  public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
  public UserStatus getStatus() { return status; }

}
