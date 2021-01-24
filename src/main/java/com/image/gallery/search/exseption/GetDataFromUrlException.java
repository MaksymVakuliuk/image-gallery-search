package com.image.gallery.search.exseption;

public class GetDataFromUrlException extends RuntimeException {
    public GetDataFromUrlException(String message) {
        super(message);
    }

    public GetDataFromUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
