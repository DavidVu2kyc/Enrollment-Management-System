package com.obu.ems.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}