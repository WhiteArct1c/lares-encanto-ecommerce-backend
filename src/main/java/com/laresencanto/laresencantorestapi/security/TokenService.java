package com.laresencanto.laresencantorestapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.laresencanto.laresencantorestapi.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("lares-encanto-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);

            return token;
        }catch(JWTCreationException ex){
            throw new RuntimeException("Error while generating token", ex);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("lares-encanto-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch(JWTVerificationException ex){
            return "";
        }
    }

    public DecodedJWT decodedJwtToken (String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
