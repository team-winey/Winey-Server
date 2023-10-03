package org.winey.server.infrastructure;

import java.util.List;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.block.BlockUser;
import org.winey.server.domain.user.User;

public interface BlockUserRepository extends Repository<BlockUser, Long> {

    BlockUser save(BlockUser blockUser);

    List<BlockUser> findByRequestUser(User requestUser);
}
