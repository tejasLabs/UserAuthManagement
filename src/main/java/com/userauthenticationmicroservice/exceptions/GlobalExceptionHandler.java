package com.userauthenticationmicroservice.exceptions;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle User related exceptions and return appropriate response with status code and details of the error in the ProblemDetail spring framework class which provides a standardized format for error responses in RESTful APIs
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

    @ExceptionHandler(UserSuspendedException.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(UserSuspendedException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("User account is suspended");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ProblemDetail> handleUserExceptions(WrongPasswordException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("Wrong Credentials provided");
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

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ProblemDetail> handleEmailNotVerifiedException(EmailNotVerifiedException e,
            HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail(e.getMessage());
        problemDetail.setTitle("Email not verified");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
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

    //Possible exception when a path variable is expected to be of a certain type (e.g., UUID) but the client provides a value that cannot be converted to that type, resulting in a MethodArgumentTypeMismatchException. This handler will catch such exceptions and return a 400 Bad Request response with a message indicating the invalid path variable.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body("Invalid path variable: " + ex.getValue());
    }

    // Handle validation exceptions that occur when method parameters annotated with validation constraints (e.g., @NotNull, @Size) are violated, resulting in a ConstraintViolationException. This handler will catch such exceptions and return a 400 Bad Request response with a message indicating the validation failure.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body("Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ProblemDetail> handleCredentialsExpired(CredentialsExpiredException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail("Credentials have expired. Please change your password to continue.");
        problemDetail.setTitle("Credentials Expired");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientAuthentication(InsufficientAuthenticationException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail("Insufficient details for authentication.");
        problemDetail.setTitle("Insufficient Authentication");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProviderNotFound(ProviderNotFoundException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setDetail("Authentication provider not found.");
        problemDetail.setTitle("Provider Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationCredentialsNotFound(AuthenticationCredentialsNotFoundException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail("Authentication credentials not found.");
        problemDetail.setTitle("Authentication Credentials Not Found");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationServiceException(AuthenticationServiceException e, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("Authentication service error occurred.");
        problemDetail.setTitle("Authentication Service Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    // Fallback handler for any other unhandled exceptions that may occur in the application, returning a generic error response with status code 500 Internal Server Error and the exception message in the response body.
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
