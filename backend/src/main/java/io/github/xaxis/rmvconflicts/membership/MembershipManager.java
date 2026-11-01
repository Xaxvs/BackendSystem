package io.github.xaxis.rmvconflicts.membership;

import io.github.xaxis.rmvconflicts.api.exceptions.MembershipAlreadyExistsException;
import io.github.xaxis.rmvconflicts.api.exceptions.MembershipNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.api.pageable.PageableUtils;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectRepository;
import io.github.xaxis.rmvconflicts.specifications.UserMembershipSpecifications;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipManager {

  private final MembershipRepository membershipRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public MembershipManager(
      MembershipRepository membershipRepository,
      ProjectRepository projectRepository,
      UserRepository userRepository) {
    this.membershipRepository = membershipRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  public UserMembership getMembership(Long id) {
    return membershipRepository.findById(id).orElseThrow(() -> new MembershipNotFoundException(id));
  }

  public UserMembership getMembership(Long projectId, Long userId) {
    return membershipRepository
        .findByProject_IdAndUser_Id(projectId, userId)
        .orElseThrow(() -> new MembershipNotFoundException(userId));
  }

  @Transactional
  public void deleteMembership(Long id) {
    UserMembership membership = getMembership(id);
    membership.getProject().removeMember(membership);
    membershipRepository.delete(membership);
  }

  @Transactional
  public void deleteMembership(Long projectId, Long userId) {
    UserMembership membership =
        membershipRepository
            .findByProject_IdAndUser_Id(projectId, userId)
            .orElseThrow(() -> new MembershipNotFoundException(userId));
    membership.getProject().removeMember(membership);
    membershipRepository.delete(membership);
  }

  @Transactional
  public UserMembership createMembership(Long projectId, Long userId, ProjectRole projectRole) {
    if (membershipRepository.existsByProject_IdAndUser_Id(projectId, userId)) {
      throw new MembershipAlreadyExistsException("Membership already exists in project");
    }

    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return project.addMember(user, projectRole);
  }

  public UserMembership updateMembershipRole(Long projectId, Long userId, ProjectRole newRole) {
    UserMembership membership = getMembership(projectId, userId);
    membership.setRole(newRole);
    return membershipRepository.save(membership);
  }

  public Page<UserMembership> findMemberships(
      int page,
      int size,
      String direction,
      String sortBy,
      String projectName,
      String userName,
      ProjectRole projectRole) {
    return membershipRepository.findAll(
        UserMembershipSpecifications.projectNameMatches(projectName)
            .and(UserMembershipSpecifications.usernameMatches(userName))
            .and(UserMembershipSpecifications.projectRoleMatches(projectRole)),
        PageableUtils.createPageable(page, size, direction, sortBy));
  }
}
