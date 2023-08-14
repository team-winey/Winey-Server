package org.winey.server.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@AllArgsConstructor
public enum NotiType {
    //등급 상승
    RANKUPTO2("기사가 되신 걸 축하해요!"),
    RANKUPTO3("귀족이 되신 걸 축하해요!"),
    RANKUPTO4("황제가 되신 걸 축하해요!"),

    //삭제로 등급 강등
    DELETERANKDOWNTO3("게시글이 삭제되어 등급이 귀족으로 내려갔어요."),
    DELETERANKDOWNTO2("게시글이 삭제되어 등급이 기사로 내려갔어요."),

    DELETERANKDOWNTO1("게시글이 삭제되어 등급이 귀족으로 내려갔어요."),

    //목표 달성 실패
    GOALFAILED("이번에는 아쉽지만 힘내서 다음 목표를 세워볼까요?"),

    //좋아요 알림 -> 내가 좋아요 누른건 알림 안주기
    LIKENOTI("님이 회원님의 게시글을 좋아해요."),

    //피드에 댓글 알림 -> 내가 댓글 쓴건 알림 안주기
    COMMENTNOTI("님이 회원님의 게시글에 댓글을 남겼어요."),

    //처음 로그인했을 때 생성하는 알림
    HOWTOLEVELUP("위니의 캐릭터 레벨업 방법을 알아볼까요?");

    private final String type;

}

