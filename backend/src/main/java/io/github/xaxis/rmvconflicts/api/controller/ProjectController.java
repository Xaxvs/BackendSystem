package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.dto.request.ProjectCreationRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.ProjectResponse;
import io.github.xaxis.rmvconflicts.api.pageable.PageResponse;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectManager;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectManager projectManager;

  public ProjectController(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  public ProjectResponse toProjectResponse(Project project) {
    return new ProjectResponse(
        project.getId(),
        project.getActions(),
        project.getMembers(),
        project.getName(),
        project.getLastActivityAt(),
        project.getCreatedAt(),
        project.getOwner().getId(),
        project.getOwner().getUsername(),
        project.getGithubRepositoryUrl());
  }

  @DeleteMapping("/{id}")
  public void deleteProject(@PathVariable Long id) {
    projectManager.deleteProject(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
    Project project = projectManager.getProject(id);
    return ResponseEntity.ok(toProjectResponse(project));
  }

  @PostMapping
  public ResponseEntity<ProjectResponse> createProject(
      @Valid @RequestBody ProjectCreationRequest request) {
    ProjectResponse project =
        projectManager.createProject(
            request.name(), request.ownerId(), request.githubRepositoryUrl());

    return ResponseEntity.status(HttpStatus.CREATED).body(project);
  }

  @GetMapping
  public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "last_activity_at") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String ownerUsername,
      @RequestParam(required = false) String githubRepositoryUrl,
      @RequestParam(required = false) String query) {

    Page<Project> tickets =
        projectManager.findProjects(
            page, size, direction, sortBy, query, ownerUsername, githubRepositoryUrl);

    PageResponse<ProjectResponse> request =
        new PageResponse<>(
            tickets.getContent().stream().map(this::toProjectResponse).toList(),
            tickets.getNumber(),
            tickets.getSize(),
            tickets.getTotalPages(),
            tickets.getTotalElements(),
            tickets.isFirst(),
            tickets.isLast());

    return ResponseEntity.ok(request);
  }
}
