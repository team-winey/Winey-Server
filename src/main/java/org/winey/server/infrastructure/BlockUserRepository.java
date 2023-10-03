package org.winey.server.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.block.BlockUser;
import org.winey.server.domain.user.User;

public interface BlockUserRepository extends Repository<BlockUser, Long> {

    BlockUser save(BlockUser blockUser);

    @Query("select bu from BlockUser bu join fetch bu.responseUser where bu.requestUser = :requestUser")
    List<BlockUser> findByRequestUser(User requestUser);

    void deleteByRequestUser(User requestUser);

    void deleteByResponseUser(User responseUser);
}
