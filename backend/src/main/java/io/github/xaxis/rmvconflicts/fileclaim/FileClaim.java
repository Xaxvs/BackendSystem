package io.github.xaxis.rmvconflicts.fileclaim;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "file_claims")
public class FileClaim {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  // todo add path, duration, project, user, file or folder, claim state

  protected FileClaim() {}
}
