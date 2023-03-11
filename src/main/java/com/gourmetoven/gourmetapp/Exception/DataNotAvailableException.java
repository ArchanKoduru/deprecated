package com.gourmetoven.gourmetapp.Exception;

public class DataNotAvailableException extends RuntimeException {
    public DataNotAvailableException() {
        super();
    }

    public DataNotAvailableException(String message) {
        super(message);
    }
}
