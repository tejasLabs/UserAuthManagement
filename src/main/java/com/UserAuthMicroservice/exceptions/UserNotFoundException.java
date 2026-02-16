package com.UserAuthMicroservice.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("No account exists for that email address.");
    }
}
