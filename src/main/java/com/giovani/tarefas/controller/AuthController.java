package com.giovani.tarefas.controller;

import com.giovani.tarefas.dto.AuthenticationRequest;
import com.giovani.tarefas.dto.AuthenticationResponse;
import com.giovani.tarefas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest request){
        return service.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody AuthenticationRequest request){
        service.register(request);
    }
}
