package com.resourcemind.app.security;

import com.resourcemind.app.domain.User;
import com.resourcemind.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//            log.info("Processing JWT authentication for request: {}", request.getRequestURI());
//            final String requestTokenHeader = request.getHeader("Authorization");
//            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
//                log.warn("JWT Token does not begin with Bearer String");
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            String token = requestTokenHeader.split("Bearer ")[1];
//            String username = authUtil.getUsernameFromToken(token);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User user = userRepository.findByUsername(username).orElse(null);
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
////            log.info("JWT authentication successful for user: {}", username);
//            }
//            filterChain.doFilter(request, response);
//
////        catch ( Exception e) {
////            log.error("Error processing JWT authentication: {}", e.getMessage());
////            filterChain.doFilter(request, response);
////        }
//    }
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    try {
        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username = authUtil.getUsernameFromToken(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);

    } catch (Exception e) {
        // 👇 THIS is what you were missing
//        log.error("JWT error: {}", e.getMessage());
//
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().write("Token expired or invalid");
        log.error("JWT error: {}", e.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String json = """
        {
            "error": "Unauthorized",
            "message": "Token expired or invalid"
        }
        """;

        response.getWriter().write(json);
        return;

//        return; // 🚫 STOP the chain
    }
}
}


