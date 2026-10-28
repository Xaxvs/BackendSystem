package io.github.xaxis.rmvconflicts.membership;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<UserMembership, Long> {

  boolean existsByProject_IdAndUser_Id(Long projectId, Long userId);
}
