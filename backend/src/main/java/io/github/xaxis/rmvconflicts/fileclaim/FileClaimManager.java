package io.github.xaxis.rmvconflicts.fileclaim;

import io.github.xaxis.rmvconflicts.api.exceptions.FileClaimNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectRepository;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
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
}
