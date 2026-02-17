package com.userauthenticationmicroservice.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already has an account with this email!");
    }
    
}
