package io.github.xaxis.rmvconflicts.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MembershipRepository extends JpaRepository<UserMembership, Long>, JpaSpecificationExecutor<UserMembership> {

  boolean existsByProject_IdAndUser_Id(Long projectId, Long userId);
}
