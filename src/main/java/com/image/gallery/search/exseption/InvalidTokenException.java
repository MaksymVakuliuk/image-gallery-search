package com.image.gallery.search.exseption;

import java.time.ZonedDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidTokenException {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public InvalidTokenException(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }
}
