package com.ringle.courseregistration.global.exception;

public abstract class DataExistException extends RuntimeException {

    protected DataExistException(String message) {
        super(message);
    }
}