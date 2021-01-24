package com.image.gallery.search.exseption;

public class MyIoException extends RuntimeException {
    public MyIoException(String message) {
        super(message);
    }

    public MyIoException(String message, Throwable cause) {
        super(message, cause);
    }
}
