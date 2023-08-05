package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.comment.CreateCommentResponseDto;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.domain.comment.Comment;
import org.winey.server.infrastructure.CommentRepository;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreateCommentResponseDto createComment(Long userId, Long feedId, String content) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));


        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .feed(feed)
                .build();
        commentRepository.save(comment);
        return CreateCommentResponseDto.of(feedId, (long) commentRepository.countByFeed(feed),user);
    }


}
