package com.demo.auth.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends HttpException {
    private String details;

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, String details) {
        this(message);
        this.details = details;
    }
}
