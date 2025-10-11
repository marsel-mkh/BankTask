package com.t1.marselmkh.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenBuilder {

    @Value("${security.token}")
    private String jwtSecret;

    @Value("${security.expiration}")
    private int jwtExpirationMs;

    public String generateServiceToken(String serviceName) {

        return Jwts.builder()
                .setSubject(serviceName)
                .claim("type", "SERVICE")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}