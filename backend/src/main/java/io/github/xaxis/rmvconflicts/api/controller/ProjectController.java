package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.PageResponse;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectManager;
import io.github.xaxis.rmvconflicts.api.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        project.getName(),
        project.getOwner().getId(),
        project.getOwner().getUsername(),
        project.getGithubRepositoryUrl()
    );
  }

  @GetMapping
  public ResponseEntity<PageResponse<ProjectResponse>> getAgents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "last_activity_at") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String ownerUsername,
      @RequestParam(required = false) String githubRepositoryUrl,
      @RequestParam(required = false) String q
  ) {

    Page<Project> tickets = projectManager.findProjects(
        page, size, direction,
        sortBy, q, ownerUsername,
        githubRepositoryUrl
    );

    PageResponse<ProjectResponse> request = new PageResponse<>(
        tickets.getContent().stream().map(this::toProjectResponse).toList(),
        tickets.getNumber(),
        tickets.getSize(),
        tickets.getTotalPages(),
        tickets.getTotalElements(),
        tickets.isFirst(),
        tickets.isLast()
    );

    return ResponseEntity.ok(request);
  }

}
