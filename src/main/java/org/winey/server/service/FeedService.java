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
        System.out.println("여기1");
        Goal myGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst()
                .orElseThrow(()-> new ForbiddenException(Error.FEED_FORBIDDEN_EXCEPTION, Error.FEED_FORBIDDEN_EXCEPTION.getMessage())); //목표 설정 안하면 피드 못만듬 -> 에러처리
        System.out.println("여기2");
        myGoal.setDuringGoalCount(myGoal.getDuringGoalCount()+1); //기간 중 달성한 목표개수 늘리기
        System.out.println("여기3");
        updateDuringGoalByCreateFeed(myGoal,feed.getFeedMoney(),presentUser);
        System.out.println("여기4");

        return CreateFeedResponseDto.of(feed.getId(),feed.getCreatedAt());
    }
    private void updateDuringGoalByCreateFeed(Goal presentGoal, Long feedMoney, User presentUser){
        updateUserGoalAmount(presentUser, feedMoney,true); //누적 금액 피드 금액 업데이트
        if (!presentGoal.isAttained()){ //목표달성을 못한 상태인데
            if (LocalDate.now().isBefore(presentGoal.getTargetDate())){ //타켓날짜를 지나지 않았으면
                if (presentGoal.getTargetMoney() <= presentGoal.getDuringGoalAmount()){ //현재까지 누적 + 피드가격으로 업데이트된 금액 >= 목표금액
                    presentGoal.setAttained(true); // 달성여부 체크
                    checkUserLevelUp(presentUser); // userLevel 변동사항 체크
                }
            }

        }
    }
    private void checkUserLevelUp(User presentUser) {
        int userAchievedGoals = goalRepository.countByUserAndIsAttained(presentUser,true); //Goal 중 userid가 맞고 isAttained true 개수 세기
        switch (userAchievedGoals){
            case 1:
                presentUser.setUserLevel(UserLevel.KNIGHT);
                break;
            case 3:
                presentUser.setUserLevel(UserLevel.ARISTOCRAT);
                break;
            case 9:
                presentUser.setUserLevel(UserLevel.EMPEROR);
                break;
            default:
                break;
        }
    }
    private void updateUserGoalAmount(User presentUser, Long feedMoney,Boolean upOrDown){ //true : 돈 올리기, down : 내리기
        List<Goal> wantUpdateGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser);
        Goal myGoal = wantUpdateGoal.stream().findFirst().get();
        if (upOrDown){
            myGoal.setDuringGoalAmount(myGoal.getDuringGoalAmount()+feedMoney);
        }
        else{
            myGoal.setDuringGoalAmount(myGoal.getDuringGoalAmount()-feedMoney);
        }
    }
}
