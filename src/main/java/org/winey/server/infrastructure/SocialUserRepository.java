package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.user.SocialType;
import org.winey.server.domain.user.SocialUser;

import java.util.Optional;

public interface SocialUserRepository extends Repository<SocialUser, Long> {
    void save(SocialUser socialUser);

    Optional<SocialUser> findByEmailAndSocialType(String email, SocialType socialType);



    boolean existsByEmailAndSocialType(String email, SocialType socialType);



}
