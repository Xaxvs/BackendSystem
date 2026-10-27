package io.github.xaxis.rmvconflicts.api.controller;

import io.github.xaxis.rmvconflicts.api.PageResponse;
import io.github.xaxis.rmvconflicts.api.dto.request.UserRequest;
import io.github.xaxis.rmvconflicts.api.dto.response.UserResponse;
import io.github.xaxis.rmvconflicts.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserManager userManager;

  public UserController(UserManager userManager) {
    this.userManager = userManager;
  }


  public UserResponse toUserResponse(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable @NotNull Long id) {
    Optional<User> optionalUser = userManager.getUserById(id);

    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    userManager.deleteUser(id);
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
    User user = userManager.createUser(userRequest.displayName(), userRequest.email());
    return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
  }

  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable @NotNull Long id) {
    Optional<User> optionalUser = userManager.getUserById(id);

    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    User user = optionalUser.get();

    return toUserResponse(user);
  }

  @GetMapping
  public ResponseEntity<PageResponse<UserResponse>> getAgents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "displayName") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) UserStatus userStatus,
      @RequestParam(required = false) String q
  ) {

    Page<User> tickets = userManager.findUsers(page, size, direction, sortBy, userStatus, q);

    PageResponse<UserResponse> request = new PageResponse<>(
        tickets.getContent().stream().map(this::toUserResponse).toList(),
        tickets.getNumber(),
        tickets.getSize(),
        tickets.getTotalPages(),
        tickets.getTotalElements(),
        tickets.isFirst(),
        tickets.isLast()
    );

    return ResponseEntity.ok(request);
  }

}

