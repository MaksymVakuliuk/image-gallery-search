package com.image.gallery.search.exseption;

public class InvalidTokenResponseException extends RuntimeException {
    public InvalidTokenResponseException(String message) {
        super(message);
    }

    public InvalidTokenResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
