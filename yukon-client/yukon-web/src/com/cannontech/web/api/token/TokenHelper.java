package com.cannontech.web.api.token;

import java.security.Key;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class TokenHelper {

    private static Key secretKey ;
    private static long tokenValidityInMilliSeconds = 1800000; // 30 min
    private static final String BEARER = "Bearer ";

    @PostConstruct
    protected void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public static String createToken(Integer userId) {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expirationDateTime = now.plus(tokenValidityInMilliSeconds, ChronoUnit.MILLIS);

        Date issueDate = Date.from(now.toInstant());
        Date expirationDate = Date.from(expirationDateTime.toInstant());

        return Jwts.builder()
                   .setIssuer("Yukon") // Energy company Name
                   .setSubject(String.valueOf(userId))
                   .setAudience("Web")
                   .setIssuedAt(issueDate)
                   .setExpiration(expirationDate)
                   .signWith(secretKey)
                   .compact();
 
    }

    public static String resolveToken(HttpServletRequest req) {

        String token = Optional.ofNullable(req.getHeader("Authorization"))
                               .filter(s -> s.length() > BEARER.length() && s.startsWith(BEARER))
                               .map(s -> s.substring(BEARER.length(), s.length()))
                               .orElseThrow(() -> new AuthenticationException("No JWT token found in request headers"));

        return token;
    }

    public static String getUserId(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    private static Claims getAllClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                                .setSigningKey(secretKey)
                                .parseClaimsJws(token)
                                .getBody();
            return claims;
        } catch (IllegalArgumentException | JwtException ex) {
            throw new AuthenticationException("Expired or invalid JWT token");
        }

    }

    public static boolean checkExpiredJwt(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (ExpiredJwtException expiredEx) {
            // "JWT Token is expired"
            return true;
        }
        return false;
    }

}
