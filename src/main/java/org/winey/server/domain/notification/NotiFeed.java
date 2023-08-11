package org.winey.server.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotiFeed {

    //좋아요 알림 -> 내가 좋아요 누른건 알림 안주기
    LIKENOTI("님이 회원님의 게시글을 좋아해요."),

    //피드에 댓글 알림 -> 내가 댓글 쓴건 알림 안주기
    COMMENTNOTI("님이 회원님의 게시글에 댓글을 남겼어요.");

    private final String type;

}
