package io.github.xaxis.rmvconflicts.api.exceptions;

public class FileActivityNotFoundException extends RuntimeException {
  public FileActivityNotFoundException(Long id) {
    super("FileActivity not found with id: " + id);
  }
}
