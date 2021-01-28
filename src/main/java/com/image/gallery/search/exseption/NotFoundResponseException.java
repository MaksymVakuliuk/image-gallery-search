package com.image.gallery.search.exseption;

public class NotFoundResponseException extends RuntimeException {
    public NotFoundResponseException(String message) {
        super(message);
    }

    public NotFoundResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
