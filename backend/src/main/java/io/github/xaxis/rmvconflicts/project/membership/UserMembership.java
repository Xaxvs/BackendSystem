package io.github.xaxis.rmvconflicts.project.membership;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "user_memberships",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_project_membership_project_user",
        columnNames = {"project_id", "user_id"}
    )
)
public class UserMembership {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  private ProjectRole role;

  protected UserMembership() {
  }

  public UserMembership(Project project, User user, ProjectRole role) {
    this.project = project;
    this.user = user;
    this.role = role;
  }
}
