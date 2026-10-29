package io.github.xaxis.rmvconflicts.project;

import io.github.xaxis.rmvconflicts.action.UserAction;
import io.github.xaxis.rmvconflicts.api.dto.response.ProjectResponse;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.api.pageable.PageableUtils;
import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import io.github.xaxis.rmvconflicts.specifications.ProjectSpecifications;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProjectManager {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public ProjectManager(ProjectRepository projectRepository, UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  public Optional<Project> getProjectById(@NotNull Long id) {
    return projectRepository.findById(id);
  }

  public Project getProject(@NotNull Long id) {
    return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
  }

  @Transactional
  public void deleteProject(@NotNull Long id) {
    Project project = getProject(id);
    projectRepository.delete(project);
  }

  @Transactional
  public ProjectResponse createProject(
      String projectName, Long ownerId, String githubRepositoryUrl) {

    User owner =
        userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));

    Project project = new Project(projectName, owner, githubRepositoryUrl);

    project.addMember(owner, ProjectRole.OWNER);

    project.addAction(UserAction.PROJECT_CREATION, owner);

    project = projectRepository.save(project);

    return new ProjectResponse(
        project.getId(),
        project.getActions(),
        project.getMembers(),
        project.getName(),
        project.getLastActivityAt(),
        project.getCreatedAt(),
        owner.getId(),
        owner.getUsername(),
        project.getGithubRepositoryUrl());
  }

  public Page<Project> findProjects(
      int page,
      int size,
      String direction,
      String sortBy,
      String query,
      String username,
      String githubRepositoryUrl) {
    return projectRepository.findAll(
        ProjectSpecifications.nameMatches(query)
            .and(ProjectSpecifications.ownerUsernameMatches(username))
            .and(ProjectSpecifications.repositoryUrlMatches(githubRepositoryUrl)),
        PageableUtils.createPageable(page, size, direction, sortBy));
  }
}
