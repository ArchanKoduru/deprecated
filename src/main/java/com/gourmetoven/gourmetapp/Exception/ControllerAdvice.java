package com.gourmetoven.gourmetapp.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(DataNotAvailableException.class)
    public ResponseEntity<ErrorResponse> DataNotAvailableException(
            Exception ex
    ) {
        HttpStatus status = NOT_FOUND;
        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        ex.getMessage()
                ),
                status
        );
    }

    @ExceptionHandler(AccessErrorException.class)
    public ResponseEntity<ErrorResponse> AccessErrorException(
            Exception e
    ) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        e.getMessage()
                ),
                status
        );
    }

    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<ErrorResponse> DataFormatException(
            Exception e
    ) {
        HttpStatus status = HttpStatus.EXPECTATION_FAILED;
        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        e.getMessage()
                ),
                status
        );
    }
}
