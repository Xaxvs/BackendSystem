package io.github.xaxis.rmvconflicts.action;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "actions")
public class Action {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User actor;

  @Column(name = "user_action", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private UserAction userAction;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected Action() {
  }

  public Action(UserAction userAction, Project project, User actor) {
    this.userAction = userAction;
    this.project = project;
    this.actor = actor;
    this.createdAt = Instant.now();
  }

  public void cleanup() {
    project = null;
    actor = null;
  }

  public Long getId() { return id; }
  public Project getProject() { return project; }
  public User getActor() { return actor; }
  public UserAction getUserAction() { return userAction; }
  public Instant getCreatedAt() { return createdAt; }
}
