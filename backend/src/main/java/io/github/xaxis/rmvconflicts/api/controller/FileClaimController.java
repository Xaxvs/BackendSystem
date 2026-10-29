package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.dto.request.FileClaimCreationRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.FileClaimResponse;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaim;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaimManager;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("file_claims")
public class FileClaimController {

  private final FileClaimManager fileClaimManager;

  public FileClaimController(FileClaimManager fileClaimManager) {
    this.fileClaimManager = fileClaimManager;
  }

  private FileClaimResponse toResponse(FileClaim fileClaim) {
    return new FileClaimResponse(
        fileClaim.getId(),
        fileClaim.getPath(),
        fileClaim.getCreatedAt(),
        fileClaim.getExpiresAt(),
        fileClaim.getReleasedAt(),
        fileClaim.getProject().getId(),
        fileClaim.getUser().getId(),
        fileClaim.getFileType(),
        fileClaim.getClaimState());
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileClaimResponse> getFileClaim(@PathVariable @NotNull Long id) {
    FileClaim fileClaim = fileClaimManager.getFileClaim(id);
    return ResponseEntity.ok(toResponse(fileClaim));
  }

  @DeleteMapping("/{id}")
  public void deleteFileClaim(@PathVariable @NotNull Long id) {
    fileClaimManager.deleteFileClaim(id);
  }

  @PostMapping
  public ResponseEntity<FileClaimResponse> createFileClaim(
      @RequestBody @NotNull FileClaimCreationRequest creationRequest) {

    FileClaim fileClaim =
        fileClaimManager.createFileClaim(
            creationRequest.path(),
            creationRequest.expiresAt(),
            creationRequest.projectId(),
            creationRequest.userId(),
            creationRequest.fileType());

    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(fileClaim));
  }
}
