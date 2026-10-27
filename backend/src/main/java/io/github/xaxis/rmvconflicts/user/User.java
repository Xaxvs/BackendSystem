package io.github.xaxis.rmvconflicts.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "created_at")
  private Instant createdAt;

  @Getter
  @Setter
  @Column(nullable = false, unique = true, name = "username")
  private String username;

  @Getter
  @Setter
  @Column(nullable = false)
  private String email;

  @Getter
  @Setter
  @Column(name = "last_activity_at")
  private Instant lastActivityAt;

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  protected User() {
  }

  public User(String username, String email) {
    this.username = username.toLowerCase(Locale.ROOT);
    this.email = email.toLowerCase(Locale.ROOT);
  }

  public void setActive(UserStatus userStatus) {
    status = UserStatus.CLAIM_ACTIVE;
  }

  public void setIdle(UserStatus userStatus) {
    status = UserStatus.IDLE;
  }

}
