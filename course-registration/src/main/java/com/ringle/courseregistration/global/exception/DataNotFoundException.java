package com.ringle.courseregistration.global.exception;

public abstract class DataNotFoundException extends RuntimeException {

    protected DataNotFoundException(String message) {
        super(message);
    }
}