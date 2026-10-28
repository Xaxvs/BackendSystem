package io.github.xaxis.rmvconflicts.membership;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
    name = "user_memberships",
    uniqueConstraints = @UniqueConstraint(
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
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Getter
  @OnDelete(action = OnDeleteAction.CASCADE)
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

  public void cleanup() {
    project = null;
    user = null;
  }
}
