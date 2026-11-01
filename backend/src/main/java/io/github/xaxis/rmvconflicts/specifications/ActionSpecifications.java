package io.github.xaxis.rmvconflicts.specifications;

import io.github.xaxis.rmvconflicts.action.Action;
import io.github.xaxis.rmvconflicts.action.UserAction;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class ActionSpecifications {

  private ActionSpecifications() {}

  public static Specification<Action> hasProjectName(String projectName) {

    return (root, query, cb) -> {
      if (projectName == null || projectName.isBlank()) {
        return cb.conjunction();
      }

      final String text = projectName.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("project").get("name")), "%" + text + "%");
    };
  }

  public static Specification<Action> hasUsername(String username) {
    return (root, query, cb) -> {
      if (username == null || username.isBlank()) {
        return cb.conjunction();
      }

      final String text = username.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("actor").get("username")), "%" + text + "%");
    };
  }

  public static Specification<Action> hasUserAction(UserAction userAction) {
    return (root, criteriaQuery, cb) -> {
      if (userAction == null) return cb.conjunction();
      return cb.equal(root.get("userAction"), userAction);
    };
  }
}
