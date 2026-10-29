package io.github.xaxis.rmvconflicts.project;

import io.github.xaxis.rmvconflicts.action.Action;
import io.github.xaxis.rmvconflicts.action.UserAction;
import io.github.xaxis.rmvconflicts.fileclaim.ClaimState;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaim;
import io.github.xaxis.rmvconflicts.fileclaim.FileType;
import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import io.github.xaxis.rmvconflicts.membership.UserMembership;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;

@Entity
@Table(name = "projects")
public class Project {

  @Getter
  @OneToMany(mappedBy = "project")
  private final List<Action> actions = new ArrayList<>();

  @Getter
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private final Set<UserMembership> members = new HashSet<>();

  @Getter
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private final Set<FileClaim> fileClaims = new HashSet<>();

  /*
  Figure out what to do about actions...
  Should they be deleted and possibly lose important information?
  Or should they be backed up, saved to their computer, or logged
  somewhere for them to look at if they need to?
  Maybe the option to delete the actions along with the project
  or keep them backed up in a log file or something.
   */
  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  protected Project() {}

  @NullMarked
  public Project(String name, User owner, String githubRepositoryUrl) {
    this.name = name;
    this.owner = owner;
    this.githubRepositoryUrl = githubRepositoryUrl;
  }

  public UserMembership addMember(User user, ProjectRole role) {
    UserMembership membership = new UserMembership(this, user, role);
    members.add(membership);
    return membership;
  }

  public void removeAction(Action action) {
    actions.remove(action);
    action.cleanup();
  }

  public Action addAction(UserAction userAction, User user) {
    Action action = new Action(userAction, this, user);
    actions.add(action);
    return action;
  }

  public void removeMember(UserMembership user) {
    members.remove(user);
    user.cleanup();
  }

  public void removeClaim(FileClaim fileClaim) {
    fileClaims.remove(fileClaim);
    fileClaim.cleanup();
  }

  public FileClaim addClaim(
      String path, Instant duration, User user, FileType fileType, ClaimState claimState) {
    FileClaim fileClaim = new FileClaim(path, duration, this, user, fileType, claimState);
    fileClaims.add(fileClaim);
    return fileClaim;
  }
}
