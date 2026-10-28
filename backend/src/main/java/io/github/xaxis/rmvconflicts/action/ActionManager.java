package io.github.xaxis.rmvconflicts.action;

import io.github.xaxis.rmvconflicts.api.exceptions.ActionNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.ProjectNotFoundException;
import io.github.xaxis.rmvconflicts.api.exceptions.UserNotFoundException;
import io.github.xaxis.rmvconflicts.project.Project;
import io.github.xaxis.rmvconflicts.project.ProjectRepository;
import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActionManager {

  private final ActionRepository actionRepository;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;

  public ActionManager(ActionRepository actionRepository, UserRepository userRepository, ProjectRepository projectRepository) {
    this.actionRepository = actionRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
  }

  public Action getAction(Long id) {
    return actionRepository.findById(id).orElseThrow(
        () -> new ActionNotFoundException(id)
    );
  }

  @Transactional
  public void deleteAction(Long id) {
    Action action = getAction(id);
    action.getProject().removeAction(action);
  }

  @Transactional
  public Action createAction(Long projectId, Long userId, UserAction userAction) {

    Project project = projectRepository.findById(projectId).orElseThrow(
        () -> new ProjectNotFoundException(projectId)
    );

    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException(userId)
    );

    Action action = project.addAction(userAction, user);

    action = actionRepository.save(action);

    return action;
  }

}
