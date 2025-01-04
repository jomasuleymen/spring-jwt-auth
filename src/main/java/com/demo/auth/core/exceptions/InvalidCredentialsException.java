package com.demo.auth.core.exceptions;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Bad credentials");
    }
}
