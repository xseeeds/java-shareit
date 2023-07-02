package ru.practicum.shareit.exception.controller;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.exp.BadRequestException;
import ru.practicum.shareit.exception.exp.ConflictException;
import ru.practicum.shareit.exception.exp.NotFoundException;
import ru.practicum.shareit.exception.exp.UnknownBookingStateException;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.ValidationErrorResponse;
import ru.practicum.shareit.exception.model.Violation;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
@Generated
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse errorBadRequestException(
            final BadRequestException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "errorBadRequestException", e.getMessage());
    }

    @ExceptionHandler(UnknownBookingStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorUnknownBookingStateException(final UnknownBookingStateException e) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                e.getMessage(), "errorUnknownBookingStateException");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException.class)
    public ErrorResponse errorMismatchedInputException(
            final MismatchedInputException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "errorMismatchedInputException", e.getMessage().substring(0, endIndex));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse errorMissingServletRequestParameterException(
            final MissingServletRequestParameterException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "errorMissingServletRequestParameterException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorResponse errorMissingRequestHeaderException(
            final MissingRequestHeaderException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "errorMissingRequestHeaderException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponse errorConstraintValidationException(
            final ConstraintViolationException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final List<Violation> errorPathVariable = e.getConstraintViolations()
                .stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(errorPathVariable);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse errorMethodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final List<Violation> errorRequestBody = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(
                        error -> new Violation(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(errorRequestBody);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse errorNotFoundException(
            final NotFoundException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                "errorNotFoundException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse errorConflictException(
            final ConflictException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "errorConflictException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse errorDataIntegrityViolationException(
            final DataIntegrityViolationException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMostSpecificCause().getMessage(), ")", 2);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "errorDataIntegrityViolationException", e.getMostSpecificCause().getMessage().substring(0, endIndex + 1));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ErrorResponse errorSQLIntegrityConstraintViolationException(
            final SQLIntegrityConstraintViolationException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), ")", 2);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "errorSQLIntegrityConstraintViolationException", e.getMessage().substring(0, endIndex + 1));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ErrorResponse errorSQLSyntaxErrorException(
            final SQLSyntaxErrorException e
    ) {
        log.warn("GETAWAY => " + e.getMessage(), e);
        final int endIndex = nthIndexOf(e.getMessage(), "\n", 1);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "errorSQLSyntaxErrorException", e.getMessage().substring(0, endIndex));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse errorInternalServerErrorException(final Throwable e) {
    	log.warn("GETAWAY => " + e.getMessage(), e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "errorInternalServerErrorException", "Произошла непредвиденная ошибка => " + e.getMessage());
    }


    private int nthIndexOf(String str, String substr, int nth) {
        int pos = str.indexOf(substr);
        while (--nth > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

}
