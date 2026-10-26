package io.github.xaxis.rmvconflicts.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserManager {

  private final UserRepository userRepository;

  public UserManager(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(String displayName, String email) {
    return userRepository.save(new User(displayName, email));
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public Optional<User> getUserById(String id) {
    return userRepository.findById(id);
  }

  public Pageable createUserPageable(int page, int size, String direction, String sortBy) {
    if (page < 0 || size <= 0 || size > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad size or page: createUserPageable");
    }
    if (direction == null || Arrays.stream(Sort.Direction.values()).noneMatch(dirEnum -> dirEnum.name().toLowerCase(Locale.ROOT).equals(direction))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid direction or null: createUserPageable");
    }
    return PageRequest.of(
        page,
        size,
        Sort.by(
            Sort.Direction.fromString(direction),
            sortBy,
            "id"
        )
    );
  }

  public Page<User> findUsers(int page, int size, String direction, String sortBy) {
    return userRepository.findAll(createUserPageable(page, size, direction, sortBy));
  }
}
