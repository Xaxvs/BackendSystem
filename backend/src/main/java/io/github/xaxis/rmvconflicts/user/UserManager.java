package io.github.xaxis.rmvconflicts.user;

import io.github.xaxis.rmvconflicts.api.pageable.PageableUtils;
import io.github.xaxis.rmvconflicts.specifications.UserSpecifications;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserManager {

  private final UserRepository userRepository;

  public UserManager(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(String displayName, String email) {
    return userRepository.save(new User(displayName, email));
  }

  public void deleteUser(@NotNull Long id) {
    User user = getUser(id);
    userRepository.delete(user);
  }

  public User getUser(@NotNull Long id) {
    return getUserById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public Optional<User> getUserById(@NotNull Long id) {
    return userRepository.findById(id);
  }

  public Page<User> findUsers(
      int page, int size, String direction, String sortBy, UserStatus userStatus, String q) {
    return userRepository.findAll(
        UserSpecifications.statusMatches(userStatus)
            .and(UserSpecifications.usernameOrEmailMatches(q)),
        PageableUtils.createPageable(page, size, direction, sortBy));
  }
}
