package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.CreateFeedRequestDto;
import org.winey.server.controller.response.feed.CreateFeedResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.ForbiddenException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final GoalRepository goalRepository;

    @Transactional
    public CreateFeedResponseDto createFeed(CreateFeedRequestDto request, Long userId, String imageUrl){
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Feed feed = Feed.builder()
                .feedImage(imageUrl)
                .feedMoney(request.getFeedMoney())
                .feedTitle(request.getFeedTitle())
                .user(presentUser)
                .build();
        feedRepository.save(feed);
        Goal myGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst()
                .orElseThrow(()-> new ForbiddenException(Error.FEED_FORBIDDEN_EXCEPTION, Error.FEED_FORBIDDEN_EXCEPTION.getMessage())); //목표 설정 안하면 피드 못만듬 -> 에러처리
        if ((myGoal.isAttained() || !(LocalDate.now().isBefore(myGoal.getTargetDate())) || (myGoal.getTargetMoney() < myGoal.getDuringGoalAmount()))){ //만약 목표가 이미 달성되어있거나, 목표일자를 이미 지났거나 목표금액을 이미 넘겼다. -> 업데이트 필요없음
            System.out.println("이미 목표달성 or 목표일자 넘김 or 목표금액 넘김");
            return CreateFeedResponseDto.of(feed.getId(),feed.getCreatedAt());
        }
        myGoal.updateGoalCountAndAmount(myGoal, feed.getFeedMoney(), true); // 절약 금액, 피드 횟수 업데이트.
        myGoal.updateIsAttained(myGoal); // 달성여부 체크
        checkUserLevelUp(presentUser); // userLevel 변동사항 체크
        return CreateFeedResponseDto.of(feed.getId(),feed.getCreatedAt());
    }
    private void checkUserLevelUp(User presentUser) {
        int userAchievedGoals = goalRepository.countByUserAndIsAttained(presentUser,true); //Goal 중 userid가 맞고 isAttained true 개수 세기
        if (userAchievedGoals<1) {
            presentUser.setUserLevel(UserLevel.COMMONER);
        } else if (1<=userAchievedGoals && userAchievedGoals<3) {
            presentUser.setUserLevel(UserLevel.KNIGHT);
        } else if (3<=userAchievedGoals && userAchievedGoals<9) {
            presentUser.setUserLevel(UserLevel.ARISTOCRAT);
        } else {
            presentUser.setUserLevel(UserLevel.EMPEROR);
        }
    }
}
