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

import jdk.jfr.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;

@Entity
@Table(name = "projects")
public class Project {

  @OneToMany(mappedBy = "project")
  /*
  Figure out what to do about actions...
  Should they be deleted and possibly lose important information?
  Or should they be backed up, saved to their computer, or logged
  somewhere for them to look at if they need to?
  Maybe the option to delete the actions along with the project
  or keep them backed up in a log file or something.
   */
  private final List<Action> actions = new ArrayList<>();

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private final Set<UserMembership> members = new HashSet<>();

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @Column(nullable = false, name = "file_claims")
  private final Set<FileClaim> fileClaims = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Timestamp
  @Column(name = "last_activity_at", nullable = false)
  private Instant lastActivityAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(name = "github_repository_url", nullable = false)
  private String githubRepositoryUrl;

  protected Project() {}

  @NullMarked
  public Project(String name, User owner, String githubRepositoryUrl) {
    this.name = name;
    this.owner = owner;
    this.githubRepositoryUrl = githubRepositoryUrl;
    this.lastActivityAt = Instant.now();
  }

  public UserMembership addMember(User user, ProjectRole role) {
    UserMembership membership = new UserMembership(this, user, role);
    members.add(membership);
    return membership;
  }

  public void removeAction(Action action) {
    actions.remove(action);
  }

  public Action addAction(UserAction userAction, User user) {
    Action action = new Action(userAction, this, user);
    actions.add(action);
    return action;
  }

  public void removeMember(UserMembership user) {
    members.remove(user);
  }

  public void removeClaim(FileClaim fileClaim) {
    fileClaims.remove(fileClaim);
  }

  public FileClaim addClaim(
      String path, Instant duration, User user, FileType fileType, ClaimState claimState) {
    FileClaim fileClaim = new FileClaim(path, duration, this, user, fileType, claimState);
    fileClaims.add(fileClaim);
    return fileClaim;
  }

  public List<Action> getActions() { return actions; }
  public Set<UserMembership> getMembers() { return members; }
  public Set<FileClaim> getFileClaims() { return fileClaims; }
  public Long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Instant getLastActivityAt() { return lastActivityAt; }
  public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
  public Instant getCreatedAt() { return createdAt; }
  public User getOwner() { return owner; }
  public void setOwner(User owner) { this.owner = owner; }
  public String getGithubRepositoryUrl() { return githubRepositoryUrl; }
  public void setGithubRepositoryUrl(String githubRepositoryUrl) {
    this.githubRepositoryUrl = githubRepositoryUrl;
  }
}
