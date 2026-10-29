package io.github.xaxis.rmvconflicts.api.exceptions;

public class FileClaimNotFoundException extends RuntimeException {
  public FileClaimNotFoundException(long id) {
    super("No file claim found with id: " + id);
  }
}
