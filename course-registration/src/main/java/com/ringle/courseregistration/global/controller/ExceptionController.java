package com.ringle.courseregistration.global.controller;

import com.ringle.courseregistration.global.dto.ExceptionResponse;
import com.ringle.courseregistration.global.exception.DataExistException;
import com.ringle.courseregistration.global.exception.DataNotFoundException;
import com.ringle.courseregistration.global.exception.ForbiddenException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DataExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ExceptionResponse handleConflictException(DataExistException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ExceptionResponse handleNotFoundException(DataNotFoundException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse handleBadRequestException(BadRequestException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ExceptionResponse handleForbiddenException(ForbiddenException e) {
        return new ExceptionResponse(e.getMessage());
    }
}