package com.userauthenticationmicroservice.services;

import com.userauthenticationmicroservice.dtos.UserResponseDTO;

public interface AuthService {

    public UserResponseDTO signup(String username, String email, String password);

    public UserResponseDTO login(String usernameOrEmail, String password);
}
