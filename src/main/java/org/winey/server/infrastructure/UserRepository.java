package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.user.SocialType;
import org.winey.server.domain.user.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    // CREATE
    void save(User user);

    // READ
    Optional<User> findByUserId(Long userId);

    Boolean existsBySocialIdAndSocialType(String socialId, SocialType socialType);

    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    Optional<User> findByRefreshToken(String refreshToken);



    // UPDATE

    // DELETE
}
