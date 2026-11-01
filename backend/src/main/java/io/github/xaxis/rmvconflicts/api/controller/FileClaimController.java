package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.dto.request.FileClaimCreationRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.FileClaimResponse;
import io.github.xaxis.rmvconflicts.api.pageable.PageResponse;
import io.github.xaxis.rmvconflicts.fileclaim.ClaimState;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaim;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaimManager;
import io.github.xaxis.rmvconflicts.fileclaim.FileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.springframework.data.domain.Page;
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

  @DeleteMapping("/user/{userId}/project/{projectId}")
  public void deleteFileClaimByUserIdAndProjectId(
      @PathVariable @NotNull Long userId, @PathVariable @NotNull Long projectId) {
    fileClaimManager.deleteFileClaimByUserIdAndProjectId(userId, projectId);
  }

  @DeleteMapping("/project/{projectId}")
  public void deleteFileClaimsByProjectId(@PathVariable @NotNull Long projectId) {
    fileClaimManager.deleteFileClaimsByProjectId(projectId);
  }

  @PatchMapping("/{id}/releasedAt")
  public ResponseEntity<FileClaimResponse> releaseFileClaim(@PathVariable @NotNull Long id) {
    FileClaim fileClaim = fileClaimManager.releaseFileClaim(id);
    return ResponseEntity.ok(toResponse(fileClaim));
  }

  @PatchMapping("/{id}/expiresAt")
  public ResponseEntity<FileClaimResponse> extendFileClaim(
      @PathVariable @NotNull Long id, @RequestParam @NotNull Long additionalSeconds) {
    FileClaim fileClaim =
        fileClaimManager.extendFileClaim(id, Instant.now().plusSeconds(additionalSeconds));
    return ResponseEntity.ok(toResponse(fileClaim));
  }

  @PatchMapping("/{id}/claimState")
  public ResponseEntity<FileClaimResponse> updateFileClaimState(
      @PathVariable @NotNull Long id, @RequestParam @NotNull ClaimState claimState) {
    FileClaim fileClaim = fileClaimManager.updateFileClaimState(id, claimState);
    return ResponseEntity.ok(toResponse(fileClaim));
  }

  @PatchMapping("/{id}/fileType")
  public ResponseEntity<FileClaimResponse> updateFileClaimFileType(
      @PathVariable @NotNull Long id, @RequestParam @NotNull FileType fileType) {
    FileClaim fileClaim = fileClaimManager.updateFileClaimFileType(id, fileType);
    return ResponseEntity.ok(toResponse(fileClaim));
  }

  @PatchMapping("/{id}/path")
  public ResponseEntity<FileClaimResponse> updateFileClaimPath(
      @PathVariable @NotNull Long id, @RequestParam @NotNull String path) {
    FileClaim fileClaim = fileClaimManager.updateFileClaimPath(id, path);
    return ResponseEntity.ok(toResponse(fileClaim));
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
      @RequestBody @Valid FileClaimCreationRequest creationRequest) {

    FileClaim fileClaim =
        fileClaimManager.createFileClaim(
            creationRequest.path(),
            creationRequest.expiresAt(),
            creationRequest.projectId(),
            creationRequest.userId(),
            creationRequest.fileType());

    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(fileClaim));
  }

  @GetMapping
  public ResponseEntity<PageResponse<FileClaimResponse>> findClaims(
      @RequestParam(defaultValue = "0") @NotNull int page,
      @RequestParam(defaultValue = "20") @NotNull int size,
      @RequestParam(defaultValue = "desc") @NotNull String direction,
      @RequestParam(defaultValue = "createdAt") @NotNull String sortBy,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String projectName,
      @RequestParam(required = false) FileType fileType,
      @RequestParam(required = false) ClaimState fileClaimState) {

    Page<FileClaim> claims =
        fileClaimManager.findFileClaims(
            page, size, direction, sortBy, username, projectName, fileType, fileClaimState);

    PageResponse<FileClaimResponse> claimsPage =
        new PageResponse<>(claims.getContent().stream().map(this::toResponse).toList(), claims);

    return ResponseEntity.ok(claimsPage);
  }
}
