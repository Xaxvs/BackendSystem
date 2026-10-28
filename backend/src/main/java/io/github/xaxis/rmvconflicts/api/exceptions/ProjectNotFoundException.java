package io.github.xaxis.rmvconflicts.api.exceptions;

public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(Long projectId) {
    super("No project found with id: " + projectId);
  }

}
