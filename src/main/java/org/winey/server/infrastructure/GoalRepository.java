package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    // CREATE

    // READ
    Optional<Goal> findByUser(Long userId);
    List<Goal> findAllByUser(Long userId);

    int countByUserAndIsAttained(Long userId,boolean isAttained);

    // UPDATE

    // DELETE
}