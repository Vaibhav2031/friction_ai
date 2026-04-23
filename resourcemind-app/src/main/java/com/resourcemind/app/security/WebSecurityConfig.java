package com.resourcemind.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // public API endpoints
//                                .requestMatchers("/auth/signup", "/auth/login").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
////                        .requestMatchers("/api/v1/**").permitAll()
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/public/**").permitAll()
//                        // Swagger / OpenAPI - allow access to the UI and docs without authentication
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/v3/api-docs/**",
//                                "/v2/api-docs/**",
//                                "/v3/api-docs.yaml",
//                                "/swagger-resources/**",
//                                "/webjars/**",
//                                // also allow paths prefixed with the application's context path
//                                "/api/v1/swagger-ui/**",
//                                "/api/v1/swagger-ui.html",
//                                "/api/v1/v3/api-docs/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add the JWT authentication filter
//                .formLogin(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
