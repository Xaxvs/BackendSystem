package io.github.xaxis.rmvconflicts.user;

import org.springframework.data.jpa.domain.Specification;

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
}
