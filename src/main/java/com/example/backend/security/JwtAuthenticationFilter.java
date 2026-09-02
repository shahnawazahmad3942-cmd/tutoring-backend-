package com.example.backend.security;

/* Runs once per request: reads the Bearer token from the Authorization header,
validates it, and places the resolved user into Spring Security's context. */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService){
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) 
                                    throws ServletException, IOException {

        String token = resolveToken(request);
        
        if(token != null
           && tokenProvider.isValid(token)
           && SecurityContextHolder.getContext().getAuthentication() == null){ //Something earlier in the chain may already have authenticated the caller; a filter should never silently override that.

            String email = tokenProvider.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = 
                                 new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
            
            // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            /*
             Mark the caller as authenticated for the rest of this request; downstream
             authorization checks and @AuthenticationPrincipal read from this context.
             SecurityContextHolder.getContext().setAuthentication(authentication);

             It's stored per-thread, not globally. So it's scoped to this one request, and Spring wipes it when the request ends.
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
           }

           filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if(header != null && header.startsWith(PREFIX)) {
            return header.substring(PREFIX.length());
        }
        return null;
    }






    
}
