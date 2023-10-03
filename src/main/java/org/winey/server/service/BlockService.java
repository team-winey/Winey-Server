package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.domain.block.BlockUser;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.BlockUserRepository;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockUserRepository blockUserRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createBlockUser(Long feedId, Long userId) {
        User responseUser = feedRepository.findByFeedId(feedId).orElseThrow(
            () -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION,
                Error.NOT_FOUND_FEED_EXCEPTION.getMessage())).getUser();

        User requestUser = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        BlockUser blockUser = BlockUser.builder()
            .requestUser(requestUser)
            .responseUser(responseUser).build();

        blockUserRepository.save(blockUser);
    }
}
