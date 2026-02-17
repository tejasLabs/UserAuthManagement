package com.userauthenticationmicroservice.exceptions;

public class UserSuspendedException extends RuntimeException {
    public UserSuspendedException() {
        super("This user account has been suspended. Please contact support.");
    }
    
}
