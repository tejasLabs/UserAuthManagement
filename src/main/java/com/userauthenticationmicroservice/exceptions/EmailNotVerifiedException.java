package com.userauthenticationmicroservice.exceptions;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Email address not verified! Please verify your email before logging in.");
    }
    
}
