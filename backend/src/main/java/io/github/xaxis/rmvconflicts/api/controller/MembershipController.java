package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.dto.request.UserMembershipCreationRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.UserMembershipResponse;
import io.github.xaxis.rmvconflicts.api.pageable.PageResponse;
import io.github.xaxis.rmvconflicts.membership.MembershipManager;
import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import io.github.xaxis.rmvconflicts.membership.UserMembership;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user_memberships")
@Controller
public class MembershipController {

  private final MembershipManager membershipManager;

  public MembershipController(MembershipManager membershipManager) {
    this.membershipManager = membershipManager;
  }

  public UserMembershipResponse toResponse(UserMembership membership) {
    return new UserMembershipResponse(
        membership.getId(),
        membership.getProject().getId(),
        membership.getUser().getId(),
        membership.getRole());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserMembershipResponse> getMembership(@PathVariable @NotNull Long id) {
    UserMembership userMembership = membershipManager.getMembership(id);
    return ResponseEntity.ok(toResponse(userMembership));
  }

  @PostMapping
  public ResponseEntity<UserMembershipResponse> addMembership(
      @RequestBody @Valid UserMembershipCreationRequest creationRequest) {
    UserMembership userMembership =
        membershipManager.createMembership(
            creationRequest.projectId(), creationRequest.userId(), creationRequest.projectRole());

    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(userMembership));
  }

  @DeleteMapping("/project/{projectId}/user/{userId}")
  public ResponseEntity<Void> deleteMembership(
      @PathVariable @NotNull Long projectId, @PathVariable @NotNull Long userId) {
    membershipManager.deleteMembership(projectId, userId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMembership(@PathVariable @NotNull Long id) {
    membershipManager.deleteMembership(id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}")
  @Transactional
  public ResponseEntity<UserMembershipResponse> updateMembership(
      @PathVariable @NotNull Long id, @RequestParam @NotNull ProjectRole projectRole) {
    UserMembership userMembership = membershipManager.getMembership(id);
    userMembership.setRole(projectRole);
    return ResponseEntity.ok(toResponse(userMembership));
  }

  @PatchMapping("/project/{projectId}/user/{userId}")
  @Transactional
  public ResponseEntity<UserMembershipResponse> updateMembership(
      @PathVariable @NotNull Long projectId,
      @PathVariable @NotNull Long userId,
      @RequestParam @NotNull ProjectRole projectRole) {
    UserMembership userMembership =
        membershipManager.updateMembershipRole(projectId, userId, projectRole);
    return ResponseEntity.ok(toResponse(userMembership));
  }

  @GetMapping
  public ResponseEntity<PageResponse<UserMembershipResponse>> getMemberships(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "asc") String direction,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(required = false) String projectName,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) ProjectRole projectRole) {
    Page<UserMembership> membershipsPage =
        membershipManager.findMemberships(
            page, size, direction, sortBy, projectName, username, projectRole);
    PageResponse<UserMembershipResponse> responsePage =
        new PageResponse<>(
            membershipsPage.getContent().stream().map(this::toResponse).toList(), membershipsPage);
    return ResponseEntity.ok(responsePage);
  }
}
