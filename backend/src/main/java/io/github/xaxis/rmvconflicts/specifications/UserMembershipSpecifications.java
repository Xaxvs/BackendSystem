package io.github.xaxis.rmvconflicts.specifications;

import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import io.github.xaxis.rmvconflicts.membership.UserMembership;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class UserMembershipSpecifications {

  private UserMembershipSpecifications() {}

  public static Specification<UserMembership> projectNameMatches(String projectName) {
    return ((root, query, cb) -> {
      if (projectName == null || projectName.isBlank()) {
        return cb.conjunction();
      }

      final String text = projectName.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("project").get("name")), "%" + text + "%");
    });
  }

  public static Specification<UserMembership> projectRoleMatches(ProjectRole projectRole) {
    return ((root, query, criteriaBuilder) -> {
      if (projectRole == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("role"), projectRole);
    });
  }

  public static Specification<UserMembership> usernameMatches(String username) {
    return ((root, query, cb) -> {
      if (username == null || username.isBlank()) {
        return cb.conjunction();
      }

      final String text = username.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("user").get("username")), "%" + text + "%");
    });
  }
}
