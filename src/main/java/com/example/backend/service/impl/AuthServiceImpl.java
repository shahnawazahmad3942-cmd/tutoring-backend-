package com.example.backend.service.impl;

/* Implements registration and login: hashes passwords on signup, delegates
credential verification to Spring Security on login, and issues a JWT for both. */

import com.example.backend.service.AuthService;
import com.example.backend.dto.auth.AuthResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider tokenProvider,
        AuthenticationManager authenticationManager
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }
    

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Email Id already registered");
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        user.setEmailVerified(false);

        return buildResponse(userRepository.save(user));
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request){

        try{
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        }
        catch(AuthenticationException ex){
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return buildResponse(user);

    }


    private AuthResponse buildResponse(User user){
        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());

    }





    
}
