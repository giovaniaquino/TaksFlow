package com.giovani.tarefas.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.giovani.tarefas.exception.BusinessRuleException;
import com.giovani.tarefas.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String getToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(generateExpireDate())
                    .sign(algorithm);
        } catch (JWTCreationException ex){
            throw new BusinessRuleException("Error on JWT token generation");
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex){
            throw new BusinessRuleException("Error on JWT token verification");
        }
    }

    private Instant generateExpireDate() {
        // 5 hours
        return Instant.now().plusSeconds(18000);
    }
}
