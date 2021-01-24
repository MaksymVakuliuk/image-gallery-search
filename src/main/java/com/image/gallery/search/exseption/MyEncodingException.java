package com.image.gallery.search.exseption;

public class MyEncodingException extends RuntimeException {
    public MyEncodingException(String message) {
        super(message);
    }

    public MyEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
