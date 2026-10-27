package io.github.xaxis.rmvconflicts.project;

import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class ProjectSpecifications {

  private ProjectSpecifications() {}

  public static Specification<Project> hasRepositoryUrl(String q) {

    return (root, query, cb) -> {
      if(q == null) return cb.conjunction();

      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.or(
          cb.like(
              cb.lower(root.get("github_repository_url")),
              "%" + text + "%"
          )
      );

    };

  }

  public static Specification<Project> hasText(String q) {

    return (root, query, cb) -> {
      if(q == null) return cb.conjunction();

      final String text = q.trim().toLowerCase(Locale.ROOT);

      return cb.or(
          cb.like(
              cb.lower(root.get("name")),
              "%" + text + "%"
          )
      );

    };

  }

  public static Specification<Project> hasOwnerUsername(String username) {

    return (root, query, cb) -> {
      if (username == null) return cb.conjunction();

      final String text = username.trim().toLowerCase(Locale.ROOT);

      return cb.like(
          cb.lower(root.get("owner").get("username")),
          "%" + text + "%"
      );
    };

  }
}
