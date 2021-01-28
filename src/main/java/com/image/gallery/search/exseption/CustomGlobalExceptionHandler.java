package com.image.gallery.search.exseption;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InvalidTokenResponseException.class})
    public ResponseEntity<Object> handleTokenException(
            InvalidTokenResponseException invalidTokenResponseException) {
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        InvalidTokenException invalidTokenException =
                new InvalidTokenException(invalidTokenResponseException.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(invalidTokenException, unauthorized);
    }

    @ExceptionHandler(value = {NotFoundResponseException.class})
    public ResponseEntity<Object> handleTokenException(
            NotFoundResponseException notFoundResponseException) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        NotFoundException notFoundException =
                new NotFoundException(notFoundResponseException.getMessage(),
                        notFound,
                        ZonedDateTime.now(ZoneId.of("Z"))
                );
        return new ResponseEntity<>(notFoundException, notFound);
    }
}
