package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.user.SocialUser;

public interface SocialUserRepository extends Repository<SocialUser, Long> {

    //create
    void save(SocialUser socialUser);
}
