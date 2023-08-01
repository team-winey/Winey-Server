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

    RE_ISSUE_TOKEN_SUCCESS(HttpStatus.OK, "토큰 재발급 성공"),
    /**
     * 201 CREATED
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료됐습니다."),
    SIGNOUT_SUCCESS(HttpStatus.CREATED, "로그아웃이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(HttpStatus.CREATED, "게시물 생성이 완료됐습니다."),
    CREATE_GOAL_SUCCESS(HttpStatus.CREATED, "목표 생성이 완료됐습니다."),
    CREATE_FEED_RESPONSE_SUCCESS(HttpStatus.CREATED, "피드 반응 생성이 완료됐습니다."),

    /**
     * 204 NO CONTENT
     */
    DELETE_FEED_SUCCESS(HttpStatus.NO_CONTENT, "피드가 정상적으로 삭제되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
