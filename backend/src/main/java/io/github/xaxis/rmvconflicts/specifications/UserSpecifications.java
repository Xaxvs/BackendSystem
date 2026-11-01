package io.github.xaxis.rmvconflicts.specifications;

import io.github.xaxis.rmvconflicts.user.User;
import io.github.xaxis.rmvconflicts.user.UserStatus;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

  private UserSpecifications() {}

  public static Specification<User> statusMatches(UserStatus userStatus) {
    return ((root, query, cb) -> {
      if (userStatus == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("status"), userStatus);
    });
  }

  public static Specification<User> usernameOrEmailMatches(String q) {
    return ((root, query, cb) -> {
      if (q == null) return cb.conjunction();
      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.or(
          cb.like(cb.lower(root.get("username")), "%" + text + "%"),
          cb.like(cb.lower(root.get("email")), "%" + text + "%"));
    });
  }
}
