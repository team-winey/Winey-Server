package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.goal.Goal;

public interface GoalRepository extends Repository<Goal, Long> {
    // CREATE
    Goal save(Goal goal);

    // READ

    // UPDATE

    // DELETE
}
