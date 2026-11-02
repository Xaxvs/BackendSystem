package io.github.xaxis.rmvconflicts.activity;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "file_activity_observations")
public class FileActivity {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  @Getter
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Project project;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @Getter
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Getter
  @Setter
  @NotBlank
  private String filePath;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  private ActivityType activityType;

  @Getter
  @CreationTimestamp
  private Instant timestamp;

  protected FileActivity() {}

  public FileActivity(Project project, User user, String filePath, ActivityType activityType) {
    this.project = project;
    this.user = user;
    this.filePath = filePath;
    this.activityType = activityType;
    timestamp = Instant.now();
  }
}
