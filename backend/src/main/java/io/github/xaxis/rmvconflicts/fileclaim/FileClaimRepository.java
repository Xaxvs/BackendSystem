package io.github.xaxis.rmvconflicts.fileclaim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileClaimRepository extends JpaRepository<FileClaim, Long>, JpaSpecificationExecutor<FileClaim> {
}
