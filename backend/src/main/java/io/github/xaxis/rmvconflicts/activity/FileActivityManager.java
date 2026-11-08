package io.github.xaxis.rmvconflicts.activity;

import io.github.xaxis.rmvconflicts.api.exceptions.FileActivityNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectRepository;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileActivityManager {

  private final FileActivityRepository fileActivityRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public FileActivityManager(
      FileActivityRepository fileActivityRepository,
      ProjectRepository projectRepository,
      UserRepository userRepository) {
    this.fileActivityRepository = fileActivityRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  public FileActivity getFileActivity(Long id) {
    return fileActivityRepository
        .findById(id)
        .orElseThrow(() -> new FileActivityNotFoundException(id));
  }

  public List<FileActivity> getFileActivitiesByProject(Long projectId) {
    return fileActivityRepository.findByProject_Id(projectId);
  }

  public List<FileActivity> getFileActivitiesByUser(Long userId) {
    return fileActivityRepository.findByUser_Id(userId);
  }

  @Transactional
  public void deleteFileActivity(Long id) {
    FileActivity activity = getFileActivity(id);
    fileActivityRepository.delete(activity);
  }

  @Transactional
  public FileActivity createFileActivity(
      Long projectId, Long userId, String filePath, ActivityType activityType) {

    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    FileActivity activity = new FileActivity(project, user, filePath, activityType);
    return fileActivityRepository.save(activity);
  }

  @Transactional
  public FileActivity updateFileActivity(Long id, String filePath, ActivityType activityType) {

    FileActivity activity = getFileActivity(id);
    activity.setFilePath(filePath);
    activity.setActivityType(activityType);

    return fileActivityRepository.save(activity);
  }

  @Transactional
  public void deleteFileActivitiesByUser(Long userId) {
    fileActivityRepository.deleteByUser_Id(userId);
  }
}
