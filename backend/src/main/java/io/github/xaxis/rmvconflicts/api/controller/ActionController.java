package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.action.Action;
import io.github.xaxis.rmvconflicts.action.ActionManager;
import io.github.xaxis.rmvconflicts.api.dto.request.ActionCreationRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.ActionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("actions")
public class ActionController {

  private final ActionManager actionManager;

  public ActionController(ActionManager actionManager) {
    this.actionManager = actionManager;
  }

  public ActionResponse toResponse(Action action) {
    return new ActionResponse(
        action.getId(),
        action.getProject().getId(),
        action.getActor().getId(),
        action.getUserAction(),
        action.getCreatedAt()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ActionResponse> getAction(@PathVariable @NotNull Long id) {
    return ResponseEntity.ok(toResponse(actionManager.getAction(id)));
  }

  @DeleteMapping("/{id}")
  public void deleteAction(@PathVariable @NotNull Long id) {
    actionManager.deleteAction(id);
  }

  @PostMapping
  public ResponseEntity<ActionResponse> createAction(
      @RequestBody @Valid ActionCreationRequest creationRequest
  ) {
    Action action = actionManager.createAction(
        creationRequest.projectId(),
        creationRequest.userId(),
        creationRequest.userAction()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(action));
  }
}
