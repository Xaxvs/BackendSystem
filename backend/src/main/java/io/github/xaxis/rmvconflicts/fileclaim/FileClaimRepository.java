package io.github.xaxis.rmvconflicts.fileclaim;

import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileClaimRepository
    extends JpaRepository<FileClaim, Long>, JpaSpecificationExecutor<FileClaim> {

  void deleteAllByProject(Project project);

  void deleteAllByUserAndProject(User user, Project project);
}
