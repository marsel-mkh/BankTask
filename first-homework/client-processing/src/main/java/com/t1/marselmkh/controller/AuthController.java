package com.t1.marselmkh.controller;

import com.t1.marselmkh.dto.auth.JwtResponse;
import com.t1.marselmkh.dto.auth.LoginRequest;
import com.t1.marselmkh.service.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }
}
