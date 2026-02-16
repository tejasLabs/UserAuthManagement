package com.UserAuthMicroservice.services;

import com.UserAuthMicroservice.dtos.UserResponseDTO;

public interface IAuthService {

    public UserResponseDTO signup(String username, String email, String password);

    public UserResponseDTO login(String usernameOrEmail, String password);
}
