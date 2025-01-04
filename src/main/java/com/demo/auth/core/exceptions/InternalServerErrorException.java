package com.demo.auth.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerErrorException extends HttpException {
    private String details;

    public InternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String message, String details) {
        this(message);
        this.details = details;
    }
}
