package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    // CREATE

    // READ
    List<Goal> findAllByUser(User user);

    int countByUserAndIsAttained(User user,boolean isAttained);

    @Query("select g from Goal g where g.user = ?1 order by g.createdAt DESC")
    List<Goal> findByUserOrderByCreatedAtDesc(User user);

    // UPDATE

    // DELETE
}