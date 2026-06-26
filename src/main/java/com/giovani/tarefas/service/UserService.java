package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.AuthenticationRequest;
import com.giovani.tarefas.dto.AuthenticationResponse;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.model.enums.UserRole;
import com.giovani.tarefas.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(TokenService tokenService, AuthenticationManager authenticationManager,  UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse login(@Valid AuthenticationRequest request){
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(usernamePassword);

        User user = (User) authentication.getPrincipal();
        String token = tokenService.getToken(user);
        return new AuthenticationResponse(token);
    }

    public void register(@Valid AuthenticationRequest request){
        if (userRepository.existsByUsername(request.username())) throw new RuntimeException("Email já está em uso");

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(UserRole.USER);
        userRepository.save(newUser);
    }
}
