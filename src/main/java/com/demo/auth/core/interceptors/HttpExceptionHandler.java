package com.demo.auth.core.interceptors;

import com.demo.auth.core.exceptions.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<Object> handleHttpException(HttpException e, WebRequest request) {
        return handleExceptionInternal(
                e,
                new ResponseBody(
                        e.getStatus().value(),
                        e.getMessage()
                ),
                HttpHeaders.EMPTY,
                HttpStatusCode.valueOf(e.getStatus().value()),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBody> handleGlobalException(Exception e) {
        return ResponseEntity.internalServerError().body(new ResponseBody(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error"
        ));
    }

    public record ResponseBody(Integer status, String message) {
    }
}
