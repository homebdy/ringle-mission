package com.ringle.courseregistration.global.exception;

public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }
}