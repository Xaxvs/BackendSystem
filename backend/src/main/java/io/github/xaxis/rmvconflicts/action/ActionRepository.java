package io.github.xaxis.rmvconflicts.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionRepository extends JpaRepository<Action, Long>, JpaSpecificationExecutor<Action> {
}
