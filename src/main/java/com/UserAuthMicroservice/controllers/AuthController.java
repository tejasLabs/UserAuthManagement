package com.UserAuthMicroservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.UserAuthMicroservice.dtos.LoginRequestDTO;
import com.UserAuthMicroservice.dtos.SignupRequestDTO;
import com.UserAuthMicroservice.dtos.UserResponseDTO;
import com.UserAuthMicroservice.services.IAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    @Autowired
    private IAuthService authService;

    // Signup endpoint: returns 201 Created HttpStatus code with user details
    // (excluding password)
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request){
        UserResponseDTO newUserDTO = authService.signup(request.username(), request.email(),
                request.password());
        return ResponseEntity.status(201).body(newUserDTO);
    }

    // Login endpoint: returns 200 OK HttpStatus code with user details (excluding
    // password) if successful, or 401 Unauthorized HttpStatus code if login failed
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        UserResponseDTO loginDTO = authService.login(request.email(), request.password());
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(loginDTO);
    }

}
