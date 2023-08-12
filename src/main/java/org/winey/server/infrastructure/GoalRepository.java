package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;

import java.util.List;

public interface GoalRepository extends Repository<Goal, Long> {
    // CREATE
    Goal save(Goal goal);

    // READ

    int countByUserAndIsAttained(User user, boolean isAttained);
    @Query("SELECT g FROM Goal g WHERE g.createdAt IN (SELECT MAX(g2.createdAt) FROM Goal g2 WHERE g2.user = g.user)")
    List<Goal> findLatestGoalsForEachUser();

    @Query("select g from Goal g where g.user = ?1 order by g.createdAt DESC")
    List<Goal> findByUserOrderByCreatedAtDesc(User user);

}