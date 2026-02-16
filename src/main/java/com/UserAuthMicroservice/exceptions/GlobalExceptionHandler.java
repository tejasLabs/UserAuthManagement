package com.UserAuthMicroservice.exceptions;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle User related exceptions and return appropriate response with status
    // code and details of the error
    // in the ProblemDetail spring framework class which provides a standardized
    // format for error responses in RESTful APIs
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(UserAlreadyExistsException e,
            HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("User already exists");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(UserNotFoundException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("User not found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(WrongPasswordException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("User entered wrong password");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSubscriptionExceptions(SubscriptionNotFoundException e,
            HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("Subscription not found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidArgumentsException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        String allErrors = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    // Cast to FieldError is necessary because getAllErrors() returns ObjectError
                    // objects, and only FieldError has the getField() method.
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ":" + errorMessage;
                })
                .collect(Collectors.joining("|"));

        problemDetail.setDetail("Invalid Request:" + allErrors);
        problemDetail.setTitle("Bad Request");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // Fallback handler for any other unhandled exceptions that may occur in the
    // application, returning a generic error response with status code 500 Internal
    // Server Error and the exception message in the response body.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(Exception e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

}
