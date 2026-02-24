package com.userauthenticationmicroservice.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.userauthenticationmicroservice.dtos.SortParam;
import com.userauthenticationmicroservice.repositories.projections.UserInfo;
import com.userauthenticationmicroservice.repositories.projections.UserSummary;
import com.userauthenticationmicroservice.services.AdminService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;

    @GetMapping("/users")
    public Page<UserSummary> getAllUsers(@RequestParam(required = true,defaultValue = "0") int page, @RequestParam(required = false,defaultValue = "10") int size, @RequestParam(required = false) List<SortParam> sortParams){

        return adminService.getAllUsers(page, size, sortParams);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getUserInfo(userId));
    }

    
    
}
