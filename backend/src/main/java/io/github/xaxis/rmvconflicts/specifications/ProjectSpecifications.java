package io.github.xaxis.rmvconflicts.specifications;

import java.util.Locale;

import io.github.xaxis.rmvconflicts.project.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

  private ProjectSpecifications() {}

  public static Specification<Project> repositoryUrlMatches(String q) {

    return (root, query, cb) -> {
      if (q == null || q.isBlank()) return cb.conjunction();

      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("github_repository_url")), "%" + text + "%");
    };
  }

  public static Specification<Project> nameMatches(String q) {

    return (root, query, cb) -> {
      if (q == null || q.isBlank()) return cb.conjunction();

      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.or(cb.like(cb.lower(root.get("name")), "%" + text + "%"));
    };
  }

  public static Specification<Project> ownerUsernameMatches(String username) {

    return (root, query, cb) -> {
      if (username == null || username.isBlank()) return cb.conjunction();

      final String text = username.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("owner").get("username")), "%" + text + "%");
    };
  }
}
