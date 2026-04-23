package com.resourcemind.app.api.authentication;

import com.resourcemind.app.api.dto.LoginRequest;
import com.resourcemind.app.api.dto.LoginResponse;
import com.resourcemind.app.api.dto.SignupResponse;
import com.resourcemind.app.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody LoginRequest signupRequest) {
        // Implement signup logic here, e.g., create a new user and return a success message
        return ResponseEntity.ok(authService.signup(signupRequest));
    }
}
