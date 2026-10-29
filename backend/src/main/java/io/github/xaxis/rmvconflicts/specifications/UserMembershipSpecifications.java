package io.github.xaxis.rmvconflicts.specifications;

import io.github.xaxis.rmvconflicts.membership.UserMembership;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class UserMembershipSpecifications {

  private UserMembershipSpecifications() {}

  public static Specification<UserMembership> projectNameMatches(String projectName) {
    return ((root, query, criteriaBuilder) -> {
      if (projectName == null || projectName.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      final String text = projectName.trim().toLowerCase(Locale.ROOT);

      return criteriaBuilder.equal(root.get("project").get("name"), "%" + text + "%");
    });
  }
}
