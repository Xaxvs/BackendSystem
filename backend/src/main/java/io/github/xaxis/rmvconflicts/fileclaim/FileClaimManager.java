package io.github.xaxis.rmvconflicts.fileclaim;

import io.github.xaxis.rmvconflicts.api.exceptions.FileClaimNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.api.pageable.PageableUtils;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectRepository;
import io.github.xaxis.rmvconflicts.specifications.FileClaimSpecifications;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class FileClaimManager {

  private final FileClaimRepository fileClaimRepository;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;

  public FileClaimManager(
      FileClaimRepository fileClaimRepository,
      UserRepository userRepository,
      ProjectRepository projectRepository) {
    this.fileClaimRepository = fileClaimRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
  }

  public FileClaim getFileClaim(Long id) {
    return fileClaimRepository.findById(id).orElseThrow(() -> new FileClaimNotFoundException(id));
  }

  @Transactional
  public void deleteFileClaim(Long id) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.getProject().removeClaim(fileClaim);
    fileClaimRepository.delete(fileClaim);
  }

  @Transactional
  public void deleteFileClaimByUserIdAndProjectId(Long userId, Long projectId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
    fileClaimRepository.deleteAllByUserAndProject(user, project);
  }

  @Transactional
  public void deleteFileClaimsByProjectId(Long projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
    fileClaimRepository.deleteAllByProject(project);
  }

  @Transactional
  public FileClaim updateFileClaimState(Long id, ClaimState claimState) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.setClaimState(claimState);
    return fileClaimRepository.save(fileClaim);
  }

  @Transactional
  public FileClaim updateFileClaimPath(Long id, String path) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.setPath(path);
    return fileClaimRepository.save(fileClaim);
  }

  @Transactional
  public FileClaim extendFileClaim(Long id, Instant futureTimestamp) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.setExpiresAt(futureTimestamp);
    return fileClaimRepository.save(fileClaim);
  }

  @Transactional
  public FileClaim releaseFileClaim(Long id) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.setReleasedAt(Instant.now());
    fileClaim.setClaimState(ClaimState.RELEASED);
    return fileClaimRepository.save(fileClaim);
  }

  @Transactional
  public FileClaim createFileClaim(
      String path, Instant duration, Long projectId, Long userId, FileType fileType) {

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));

    ClaimState claimState = ClaimState.ACTIVE;

    FileClaim fileClaim = project.addClaim(path, duration, user, fileType, claimState);

    fileClaim = fileClaimRepository.save(fileClaim);

    return fileClaim;
  }

  public Page<FileClaim> findFileClaims(
      int page,
      int size,
      String direction,
      String sortBy,
      String username,
      String projectName,
      FileType fileType,
      ClaimState claimState) {
    return fileClaimRepository.findAll(
        FileClaimSpecifications.usernameMatches(username)
            .and(FileClaimSpecifications.projectNameMatches(projectName))
            .and(FileClaimSpecifications.fileTypeMatches(fileType))
            .and(FileClaimSpecifications.claimStateMatches(claimState)),
        PageableUtils.createPageable(page, size, direction, sortBy));
  }

  @Transactional
  public FileClaim updateFileClaimFileType(@NotNull Long id, @NotNull FileType fileType) {
    FileClaim fileClaim = getFileClaim(id);
    fileClaim.setFileType(fileType);
    return fileClaimRepository.save(fileClaim);
  }
}
