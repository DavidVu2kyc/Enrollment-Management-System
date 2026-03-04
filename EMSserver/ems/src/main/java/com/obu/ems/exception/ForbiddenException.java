package com.obu.ems.exception;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(message);
    }
}