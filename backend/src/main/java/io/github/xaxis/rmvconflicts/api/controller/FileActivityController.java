package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.activity.ActivityType;
import io.github.xaxis.rmvconflicts.activity.FileActivity;
import io.github.xaxis.rmvconflicts.activity.FileActivityManager;
import io.github.xaxis.rmvconflicts.api.dto.response.FileActivityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("file_activity_observations")
public class FileActivityController {

  private final FileActivityManager fileActivityService;

  public FileActivityController(FileActivityManager fileActivityService) {
    this.fileActivityService = fileActivityService;
  }

  @GetMapping("/{id}")
  public FileActivity getFileActivity(@PathVariable Long id) {
    return fileActivityService.getFileActivity(id);
  }

  @GetMapping("/project/{projectId}")
  public Iterable<FileActivityResponse> getFileActivitiesByProject(@PathVariable Long projectId) {
    return fileActivityService.getFileActivitiesByProject(projectId).stream()
        .map(this::toFileActivityResponse)
        .toList();
  }

  @PostMapping
  public ResponseEntity<FileActivityResponse> createFileActivity(
      @RequestParam Long projectId,
      @RequestParam Long userId,
      @RequestParam String filePath,
      @RequestParam ActivityType activityType) {
    FileActivityResponse response =
        toFileActivityResponse(
            fileActivityService.createFileActivity(projectId, userId, filePath, activityType));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/user/{userId}")
  public void deleteFileActivitiesByUser(@PathVariable Long userId) {
    fileActivityService.deleteFileActivitiesByUser(userId);
  }

  @GetMapping("/user/{userId}")
  public Iterable<FileActivityResponse> getFileActivitiesByUser(@PathVariable Long userId) {
    return fileActivityService.getFileActivitiesByUser(userId).stream()
        .map(this::toFileActivityResponse)
        .toList();
  }

  @DeleteMapping("/{id}")
  public void deleteFileActivity(@PathVariable Long id) {
    fileActivityService.deleteFileActivity(id);
  }

  private FileActivityResponse toFileActivityResponse(FileActivity fileActivity) {
    return new FileActivityResponse(
        fileActivity.getId(),
        fileActivity.getFilePath(),
        fileActivity.getActivityType().name(),
        fileActivity.getTimestamp().toString());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<FileActivityResponse> updateFileActivity(
      @PathVariable Long id, @RequestParam String path, @RequestParam ActivityType activityType) {
    FileActivityResponse response =
        toFileActivityResponse(fileActivityService.updateFileActivity(id, path, activityType));
    return ResponseEntity.ok(response);
  }
}
