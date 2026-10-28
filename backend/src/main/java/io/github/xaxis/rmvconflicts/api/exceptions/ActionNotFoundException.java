package io.github.xaxis.rmvconflicts.api.exceptions;

public class ActionNotFoundException extends RuntimeException {
  public ActionNotFoundException(Long actionId) {
    super("No action exists with id: " + actionId);
  }
}
