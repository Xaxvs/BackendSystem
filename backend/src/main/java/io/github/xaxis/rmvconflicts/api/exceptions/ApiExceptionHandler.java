package io.github.xaxis.rmvconflicts.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(
            "USER_NOT_FOUND",
            e.getMessage()
        ));
  }

  @ExceptionHandler(MembershipNotFoundException.class)
  public ResponseEntity<ApiError> handleMembershipNotFound(MembershipNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(
            "USER_MEMBERSHIP_NOT_FOUND",
            e.getMessage()
        ));
  }

  @ExceptionHandler(MembershipAlreadyExistsException.class)
  public ResponseEntity<ApiError> handleMembershipAlreadyExists(MembershipAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(
            "USER_MEMBERSHIP_ALREADY_EXISTS",
            e.getMessage()
        ));
  }

  @ExceptionHandler(ActionNotFoundException.class)
  public ResponseEntity<ApiError> handleActionNotFound(ActionNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(
            "USER_MEMBERSHIP_NOT_FOUND",
            e.getMessage()
        ));
  }

  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<ApiError> handleProjectNotFound(ProjectNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(
            "PROJECT_NOT_FOUND",
            e.getMessage()
        ));
  }

  @ExceptionHandler(FileClaimNotFoundException.class)
  public ResponseEntity<ApiError> handleProjectNotFound(FileClaimNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(
            "FILE_CLAIM_NOT_FOUND",
            e.getMessage()
        ));
  }
}
