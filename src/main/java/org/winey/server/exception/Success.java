package org.winey.server.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Success {

    /**
     * 200 OK
     */
    GET_RECOMMEND_LIST_SUCCESS(HttpStatus.OK, "추천 위니 전체 조회 성공"),
    GET_FEED_LIST_SUCCESS(HttpStatus.OK, "피드 전체 조회 성공"),
    GET_USER_SUCCESS(HttpStatus.OK, "유저 조회 성공"),
    GET_MYFEED_SUCCESS(HttpStatus.OK, "마이 피드 조회 성공"),
    GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "알림 리스트 조회 성공"),
    CHECK_ALL_NOTIFICATIONS(HttpStatus.OK, "알림 리스트 체크 성공"),

    GET_DETAIL_SUCCESS(HttpStatus.OK, "피드 디테일 페이지 조회 성공"),

    RE_ISSUE_TOKEN_SUCCESS(HttpStatus.OK, "토큰 재발급 성공"),
    UPDATE_NICKNAME_SUCCESS(HttpStatus.OK, "닉네임 변경 성공"),
    UPDATE_PUSH_ALLOWED_SUCCESS(HttpStatus.OK, "푸시 알림 동의 여부 변경 성공"),
    CHECK_NICKNAME_DUPLICATE_SUCCESS(HttpStatus.OK, "닉네임 중복 확인 성공"),
    CHECK_NEW_NOTIFICATION_SUCCESS(HttpStatus.OK, "새 알림 여부 조회 성공"),
    BLOCK_USER_SUCCESS(HttpStatus.OK, "유저 차단 성공"),
    GET_ACHIEVEMENT_STATUS_SUCCESS(HttpStatus.OK, "레벨 달성 현황 조회 성공"),

    /**
     * 201 CREATED
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료됐습니다."),
    SIGNOUT_SUCCESS(HttpStatus.CREATED, "로그아웃이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(HttpStatus.CREATED, "게시물 생성이 완료됐습니다."),
    CREATE_GOAL_SUCCESS(HttpStatus.CREATED, "목표 생성이 완료됐습니다."),
    CREATE_FEED_RESPONSE_SUCCESS(HttpStatus.CREATED, "피드 반응 생성이 완료됐습니다."),
    CREATE_COMMENT_RESPONSE_SUCCESS(HttpStatus.CREATED, "댓글 생성이 완료됐습니다."),


    /**
     * 204 NO CONTENT
     */
    DELETE_FEED_SUCCESS(HttpStatus.NO_CONTENT, "피드가 정상적으로 삭제되었습니다."),
    DELETE_COMMENT_SUCCESS(HttpStatus.NO_CONTENT, "댓글이 정상적으로 삭제되었습니다."),
    NOTIFICATION_EMPTY_SUCCESS(HttpStatus.NO_CONTENT, "알림이 한통도 없어요. 힝~구"),
    FCM_TOKEN_UPDATE_SUCCESS(HttpStatus.NO_CONTENT, "fcm 수정이 완료 됐습니다."),

    DELETE_USER_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴가 정상적으로 이루어졌습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
