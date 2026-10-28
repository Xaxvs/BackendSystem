package io.github.xaxis.rmvconflicts.project;

import io.github.xaxis.rmvconflicts.action.UserAction;
import io.github.xaxis.rmvconflicts.api.dto.response.ProjectResponse;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

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
    return projectRepository.findById(id).orElseThrow(
        () -> new ProjectNotFoundException(id)
    );
  }

  @Transactional
  public void deleteProject(@NotNull Long id) {
    Project project = getProject(id);
    projectRepository.delete(project);
  }

  @Transactional
  public ProjectResponse createProject(
      String projectName,
      Long ownerId,
      String githubRepositoryUrl
  ) {

    User owner = userRepository.findById(ownerId)
        .orElseThrow(() -> new UserNotFoundException(ownerId));

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
        project.getGithubRepositoryUrl()
    );
  }

  public Pageable createProjectPageable(int page, int size, String direction, String sortBy) {
    if (page < 0 || size <= 0 || size > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad size or page: createProjectPageable");
    }
    if (direction == null || Arrays.stream(Sort.Direction.values()).noneMatch(dirEnum -> dirEnum.name().toLowerCase(Locale.ROOT).equals(direction))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid direction or null: createProjectPageable");
    }
    return PageRequest.of(
        page,
        size,
        Sort.by(
            Sort.Direction.fromString(direction),
            sortBy,
            "id"
        )
    );
  }

  public Page<Project> findProjects(
      int page, int size, String direction,
      String sortBy, String q, String username,
      String githubRepositoryUrl
  ) {
    return projectRepository.findAll(
        ProjectSpecifications.hasText(q)
            .and(ProjectSpecifications.hasOwnerUsername(username))
            .and(ProjectSpecifications.hasRepositoryUrl(githubRepositoryUrl)),
        createProjectPageable(page, size, direction, sortBy)
    );
  }
}
