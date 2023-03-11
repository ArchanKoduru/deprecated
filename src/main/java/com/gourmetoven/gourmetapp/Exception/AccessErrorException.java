package com.gourmetoven.gourmetapp.Exception;

public class AccessErrorException extends RuntimeException {
    public AccessErrorException() {
        super();
    }

    public AccessErrorException(String message) {
        super(message);
    }
}
