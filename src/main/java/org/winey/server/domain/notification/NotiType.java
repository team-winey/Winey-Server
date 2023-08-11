package org.winey.server.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotiType {
    //등급 상승
    RANKUPTO2("기사가 되신 걸 축하해요!", 2),
    RANKUPTO3("귀족이 되신 걸 축하해요!", 3),
    RANKUPTO4("황제가 되신 걸 축하해요!", 4),

    //등급 강등
    RANKDOWNTO3("귀족으로 내려갔어요.", 3),
    RANKDOWNTO2("기사로 내려갔어요.", 2),

    //좋아요 알림 -> 내가 좋아요 누른건 알림 안주기
    LIKENOTI("님이 회원님의 게시글을 좋아해요.", null),

    //피드에 댓글 알림 -> 내가 댓글 쓴건 알림 안주기
    COMMENTNOTI("님이 회원님의 게시글에 댓글을 남겼어요.", null);

    private final String type;
    private final Integer afterlevel;
}

