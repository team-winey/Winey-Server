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

    /**
     * 201 CREATED
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(HttpStatus.CREATED, "게시물 생성이 완료됐습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
