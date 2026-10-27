package io.github.xaxis.rmvconflicts.action;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "actions")
public class Action {

  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User actor;

  @Getter
  @Column(name = "user_action", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private UserAction userAction;

  @Getter
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected Action() {
  }

  public Action(UserAction userAction, Project project, User actor) {
    this.userAction = userAction;
    this.project = project;
    this.actor = actor;
  }

}
