package org.winey.server.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.CustomException;

import java.util.Objects;

@RestControllerAdvice
public class ControllerExceptionAdvice {
    /**
     * 400 BAD_REQUEST
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        return ApiResponse.error(Error.VALIDATION_REQUEST_MISSING_EXCEPTION, String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ApiResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        return ApiResponse.error(Error.VALIDATION_REQUEST_HEADER_MISSING_EXCEPTION, String.format("%s. (%s)", Error.VALIDATION_REQUEST_HEADER_MISSING_EXCEPTION.getMessage(), e.getHeaderName()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ApiResponse handleMissingRequestParameterException(final MissingServletRequestParameterException e) {
        return ApiResponse.error(Error.VALIDATION_REQUEST_PARAMETER_MISSING_EXCEPTION, String.format("%s. (%s)", Error.VALIDATION_REQUEST_PARAMETER_MISSING_EXCEPTION.getMessage(), e.getParameterName()));
    }

//    /**
//     * 500 Internal Server
//     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    protected ApiResponse<Object> handleException(final Exception e) {
//        return ApiResponse.error(Error.INTERNAL_SERVER_ERROR);
//    }

    /**
     * custom error
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse> handleSoptException(CustomException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getError(), e.getMessage()));
    }
}

