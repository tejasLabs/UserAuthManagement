package com.userauthenticationmicroservice.controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userauthenticationmicroservice.dtos.LoginRequestDTO;
import com.userauthenticationmicroservice.dtos.SignupRequestDTO;
import com.userauthenticationmicroservice.dtos.UserResponseDTO;
import com.userauthenticationmicroservice.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //Signup endpoint: returns 201 Created HttpStatus code with user details (excluding password)
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request){
        UserResponseDTO newUserDTO = authService.signup(request.username(), request.email(),
                request.password());
        return ResponseEntity.status(201).body(newUserDTO);
    }

    //Login endpoint: returns 200 OK HttpStatus code with user details (excluding password) if successful, or 401 Unauthorized HttpStatus code if login failed
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        UserResponseDTO loginDTO = authService.login(request.email(), request.password());
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(loginDTO);
    }

}
