package com.demo.auth.core.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpException extends RuntimeException {
    private final String message;
    private final HttpStatus status;
}
