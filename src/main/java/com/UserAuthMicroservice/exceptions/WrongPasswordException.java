package com.UserAuthMicroservice.exceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Entered password is incorrect! Please try again.");
    }

}
