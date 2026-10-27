package io.github.xaxis.rmvconflicts.project;

import io.github.xaxis.rmvconflicts.action.Action;
import io.github.xaxis.rmvconflicts.project.membership.UserMembership;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Action> actions = new ArrayList<>();

  @Getter
  @OneToMany(mappedBy = "project")
  private final Set<UserMembership> members = new HashSet<>();

  @Getter
  @Setter
  @Column(name = "name", nullable = false)
  private String name;

  @Getter
  @Setter
  @Column(name = "last_activity_at", nullable = false)
  private Instant lastActivityAt;

  @Getter
  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @Getter
  @Setter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Getter
  @Setter
  @Column(name = "github_repository_url", nullable = false)
  private String githubRepositoryUrl;

  protected Project() {
  }

  @NullMarked
  public Project(String name, User owner, String githubRepositoryUrl) {
    this.name = name;
    this.owner = owner;
    this.githubRepositoryUrl = githubRepositoryUrl;
  }

}
