package io.github.xaxis.rmvconflicts.fileclaim;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import jdk.jfr.Timestamp;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "file_claims")
public class FileClaim {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String path;

  @CreationTimestamp
  @Column(nullable = false, name = "created_at")
  private Instant createdAt;

  @Timestamp
  @Column(nullable = false, name = "expires_at")
  private Instant expiresAt;

  @Timestamp
  @Column(name = "released_at")
  private Instant releasedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "file_type")
  private FileType fileType;

  @Column(name = "claim_state")
  @Enumerated(value = EnumType.STRING)
  private ClaimState claimState;

  public FileClaim(
      String path,
      Instant expiresAt,
      Project project,
      User user,
      FileType fileType,
      ClaimState claimState) {
    this.path = path;
    this.expiresAt = expiresAt;
    this.project = project;
    this.user = user;
    this.fileType = fileType;
    this.claimState = claimState;
  }

  protected FileClaim() {}

  public void cleanup() {
    this.project = null;
    this.user = null;
  }
}
