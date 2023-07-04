package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.winey.server.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // CREATE

    // READ
    User findByUserId(Long userId);

    // UPDATE

    // DELETE
}
