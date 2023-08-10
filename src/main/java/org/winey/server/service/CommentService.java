package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.comment.CreateCommentResponseDto;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.domain.comment.Comment;
import org.winey.server.exception.model.UnauthorizedException;
import org.winey.server.exception.model.UnprocessableEntityException;
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
        return CreateCommentResponseDto.of(comment.getCommentId(), commentRepository.countByFeed(feed),user);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Comment wantDeleteComment = commentRepository.findByCommentId(commentId)
                .orElseThrow(()-> new NotFoundException(Error.NOT_FOUND_COMMENT_EXCEPTION, Error.NOT_FOUND_COMMENT_EXCEPTION.getMessage()));
        Feed commentFeed = feedRepository.findByFeedId(wantDeleteComment.getFeed().getFeedId())
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));

        if (user.getUserId() != wantDeleteComment.getUser().getUserId() && user.getUserId() != commentFeed.getUser().getUserId() ){ //만약 현재 유저가 피드 주인도 아니고, 댓글 주인도 아니면?
                throw new UnauthorizedException(Error.DELETE_UNAUTHORIZED, Error.DELETE_UNAUTHORIZED.getMessage()); //지울 수 있는 권한이 없다.
        }
        Long res = commentRepository.deleteByCommentId(commentId);
        if (res != 1){
            throw new UnprocessableEntityException(Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION,Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION.getMessage());
        }
    }


}
