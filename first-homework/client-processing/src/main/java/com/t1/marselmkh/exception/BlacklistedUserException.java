package com.t1.marselmkh.exception;

public class BlacklistedUserException extends RuntimeException {
    public BlacklistedUserException(String message) {
        super(message);
    }
}
