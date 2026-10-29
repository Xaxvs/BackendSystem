package io.github.xaxis.rmvconflicts.api.pageable;

import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PageableUtils {

  private PageableUtils() {}

  public static Pageable createPageable(
      int page,
      int size,
      String direction,
      String sortBy) {
    if (page < 0 || size <= 0 || size > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad size or page: createPageable");
    }
    if (direction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid direction or null: createPageable");
    }

    if (!direction.equals("desc") && !direction.equals("asc")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid direction");
    }

    return PageRequest.of(page, size, Sort.by(direction, sortBy, "id"));
  }
}
