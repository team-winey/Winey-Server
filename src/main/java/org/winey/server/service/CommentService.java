package org.winey.server.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.common.message.MessageQueueSender;
import org.winey.server.controller.response.comment.CommentResponseDto;
import org.winey.server.controller.response.comment.DeleteCommentResponseDto;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.CustomException;
import org.winey.server.exception.model.UnauthorizedException;
import org.winey.server.exception.model.UnprocessableEntityException;
import org.winey.server.infrastructure.CommentRepository;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;
import org.winey.server.service.message.FcmRequestDto;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final UserRepository userRepository;
	private final FeedRepository feedRepository;
	private final CommentRepository commentRepository;
	private final NotiRepository notiRepository;

	private final MessageQueueSender messageQueueSender;

	@Transactional
	public CommentResponseDto createComment(Long userId, Long feedId, String content) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
				Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
		Feed feed = feedRepository.findByFeedId(feedId)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION,
				Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));

		Comment comment = Comment.builder()
			.user(user)
			.content(content)
			.feed(feed)
			.build();
		commentRepository.save(comment);

		if (user.getUserId() != feed.getUser().getUserId()) {
			createNotificationInComment(feed, comment, user); //알림 생성 후 message queue에 task 등록
		}
		return CommentResponseDto.of(comment.getCommentId(), userId, user.getNickname(), comment.getContent(),
			user.getUserLevel().getLevelNumber(), comment.getCreatedAt());
	}

	@Transactional
	public DeleteCommentResponseDto deleteComment(Long userId, Long commentId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
				Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
		Comment wantDeleteComment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_COMMENT_EXCEPTION,
				Error.NOT_FOUND_COMMENT_EXCEPTION.getMessage()));
		Feed commentFeed = feedRepository.findByFeedId(wantDeleteComment.getFeed().getFeedId())
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION,
				Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));

		if (user.getUserId() != wantDeleteComment.getUser().getUserId() && user.getUserId() != commentFeed.getUser()
			.getUserId()) { //만약 현재 유저가 피드 주인도 아니고, 댓글 주인도 아니면?
			throw new UnauthorizedException(Error.DELETE_UNAUTHORIZED,
				Error.DELETE_UNAUTHORIZED.getMessage()); //지울 수 있는 권한이 없다.
		}

		// 관련 알림 삭제
		notiRepository.deleteByNotiTypeAndResponseId(NotiType.COMMENTNOTI, wantDeleteComment.getCommentId());

		Long res = commentRepository.deleteByCommentId(commentId);
		if (res != 1) {
			throw new UnprocessableEntityException(Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION,
				Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION.getMessage());
		}
		return DeleteCommentResponseDto.of(commentId);
	}

	private void createNotificationInComment(Feed feed, Comment comment, User user) {
		Notification notification = Notification.builder() //누군가가 내 피드에 댓글을 달았어요~
			.notiReciver(feed.getUser())
			.notiType(NotiType.COMMENTNOTI)
			.notiMessage(comment.getUser().getNickname() + NotiType.COMMENTNOTI.getType())
			.isChecked(false)
			.build();
		notification.updateLinkId(feed.getFeedId());
		notification.updateResponseId(feed.getUser().getUserId());
		notification.updateRequestUserId(user.getUserId());
		notiRepository.save(notification);
		if (comment.getUser().getPushNotificationAllowed()) { //푸시알림에 동의했을 경우.
			if (notification.getNotiReceiver().getFcmToken().isEmpty())
				throw new BadRequestException(Error.INVALID_FCMTOKEN_EXCEPTION, Error.INVALID_FCMTOKEN_EXCEPTION.getMessage());
			messageQueueSender.pushSender(
				FcmRequestDto.of(
                    notification.getNotiMessage(),
                    notification.getNotiReceiver().getFcmToken(),
					notification.getNotiType(),
					feed.getFeedId()));
		}
	}

}
