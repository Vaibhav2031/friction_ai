package com.resourcemind.app.security;

import com.resourcemind.app.api.dto.LoginRequest;
import com.resourcemind.app.api.dto.LoginResponse;
import com.resourcemind.app.api.dto.SignupResponse;
import com.resourcemind.app.domain.User;
import com.resourcemind.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateToken(user);
        return new LoginResponse(token, user.getId());
    }

    public SignupResponse signup(LoginRequest signupRequest) {
        User user = userRepository.findByUsername(signupRequest.getUsername()).orElse(null);

        if(user != null){
            throw new IllegalArgumentException("Username already exists");
        }
        user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user = userRepository.save(user);
        return new SignupResponse(user.getId(), user.getUsername());
    }
}
