package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.user.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    // CREATE
    void save(User user);

    // READ
    Optional<User> findByUserId(Long userId);

    // UPDATE

    // DELETE
}
