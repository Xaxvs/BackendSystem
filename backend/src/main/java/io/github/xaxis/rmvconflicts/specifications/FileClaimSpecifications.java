package io.github.xaxis.rmvconflicts.specifications;

import io.github.xaxis.rmvconflicts.fileclaim.ClaimState;
import io.github.xaxis.rmvconflicts.fileclaim.FileClaim;
import io.github.xaxis.rmvconflicts.fileclaim.FileType;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class FileClaimSpecifications {

  private FileClaimSpecifications() {}

  public static Specification<FileClaim> usernameMatches(String username) {
    return (root, query, cb) -> {
      if (username == null || username.isBlank()) {
        return cb.conjunction();
      }

      final String text = username.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("user").get("username")), "%" + text + "%");
    };
  }

  public static Specification<FileClaim> projectNameMatches(String projectName) {
    return (root, query, cb) -> {
      if (projectName == null || projectName.isBlank()) {
        return cb.conjunction();
      }

      final String text = projectName.trim().toLowerCase(Locale.ROOT);

      return cb.like(cb.lower(root.get("project").get("name")), "%" + text + "%");
    };
  }

  public static Specification<FileClaim> claimStateMatches(ClaimState claimState) {
    return (root, query, cb) -> {
      if (claimState == null) {
        return cb.conjunction();
      }

      return cb.equal(root.get("claimState"), claimState);
    };
  }

  public static Specification<FileClaim> fileTypeMatches(FileType fileType) {
    return (root, query, cb) -> {
      if (fileType == null) return cb.conjunction();

      return cb.equal(root.get("fileType"), fileType);
    };
  }
}
