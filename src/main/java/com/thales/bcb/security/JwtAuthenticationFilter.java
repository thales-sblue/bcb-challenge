package com.thales.bcb.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.thales.bcb.modules.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try{
            DecodedJWT decodedJWT = authService.validateToken(token);
            String clientId = decodedJWT.getClaim("clientId").asString();
            String role = decodedJWT.getClaim("role").asString();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    clientId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+ role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
