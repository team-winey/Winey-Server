package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.winey.server.domain.goal.Goal;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    // CREATE
    Goal save(Goal goal);

    // READ

    // UPDATE

    // DELETE
}
