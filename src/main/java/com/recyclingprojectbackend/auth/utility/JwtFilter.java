package com.recyclingprojectbackend.auth.utility;

import com.recyclingprojectbackend.user.dto.UserDto;
import com.recyclingprojectbackend.user.model.User;
import com.recyclingprojectbackend.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtUtility jwtUtility;
    private final UserService userService;

    public JwtFilter(JwtUtility jwtUtility, UserService userService) {
        this.jwtUtility = jwtUtility;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtility.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtility.extractUserId(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDto user = userService.findUserById(userId);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            new ArrayList<>()
                    );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // The user is being set in Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
