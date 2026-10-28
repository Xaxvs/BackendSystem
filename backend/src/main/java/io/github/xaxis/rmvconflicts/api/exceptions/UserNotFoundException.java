package io.github.xaxis.rmvconflicts.api.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long userId) {
    super("No user found with id: " + userId);
  }
}
