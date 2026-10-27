package io.github.xaxis.rmvconflicts.user;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class UserSpecifications {

  private UserSpecifications() {}

  public static Specification<User> hasStatus(UserStatus userStatus) {
    return ((root, query, criteriaBuilder) -> {
      if(userStatus == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("status"), userStatus);
    });
  }

  public static Specification<User> hasText(String q) {
    return ((root, query, cb) -> {
      if(q == null) return cb.conjunction();
      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.or(
          cb.like(
              cb.lower(root.get("displayName")),
              "%"+text+"%"
          ),
          cb.like(
              cb.lower(root.get("email")),
              "%"+text+"%"
          )
      );
    });
  }
}
