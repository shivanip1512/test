package com.cannontech.web.api.token;

import java.security.Key;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;;

public class TokenHelper {

    private static Key secretKey ;
    private static Key refreshSecretKey ;
    private static long tokenValidityInMilliSeconds = 900000; // 15 min
    private static long refreshTokenValidityInMilliSeconds = 86400000;  // 24 hour
    private static final String BEARER = "Bearer";

    @PostConstruct
    protected void init() {
        //TODO: Replace with RSA signature
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        refreshSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Generate JWT token based on different claims (Issuer, Subject, Audience , IssuedAt, Expiration)
     */
    public static String createToken(Integer userId) {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expirationDateTime = now.plus(tokenValidityInMilliSeconds, ChronoUnit.MILLIS);

        Date issueDate = Date.from(now.toInstant());
        Date expirationDate = Date.from(expirationDateTime.toInstant());
        String token = Jwts.builder()
                .setIssuer("Yukon")
                .setSubject(String.valueOf(userId))
                .setAudience("Web")
                .setIssuedAt(issueDate)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
        return token;
    }
   
    /**
     * Generate refresh JWT token based on different claims (Issuer, Subject, Audience , IssuedAt, Expiration)
     */
    private static RefreshTokenDetails createRefreshToken(Integer userId, String uuid) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime refreshExpirationDateTime = now.plus(refreshTokenValidityInMilliSeconds, ChronoUnit.MILLIS);
        Date issueDate = Date.from(now.toInstant());
        Date refreshExpirationDate = Date.from(refreshExpirationDateTime.toInstant());
        String token = Jwts.builder()
                .setId(uuid)
                .setIssuer("Yukon")
                .setSubject(String.valueOf(userId))
                .setAudience("Web")
                .setIssuedAt(issueDate)
                .setExpiration(refreshExpirationDate)
                .signWith(refreshSecretKey)
                .compact();
        RefreshTokenDetails tokenDetails = new RefreshTokenDetails(getRefreshTokenId(String.valueOf(userId), uuid), token, userId);
        return tokenDetails;
    }
    
    public static RefreshTokenDetails createRefreshTokenWithUUID(Integer userId, String uuid) {
        return createRefreshToken(userId, uuid);
    }
    
    public static RefreshTokenDetails createRefreshToken(Integer userId) {
        String uuid = UUID.randomUUID().toString();
         return createRefreshToken(userId, uuid);
    }
    
    /**
     * Set access token Expiry duration and token type in token response.
     */
    public static TokenResponse setTokenTypeAndExpiresIn(TokenResponse response ) {
        response.setExpiresIn(tokenValidityInMilliSeconds);
        response.setTokenType(BEARER);
        return response;
    }
    
    /**
     * Retrieve token from request header (Authorization: Bearer <token>).
     */        
    public static String resolveToken(HttpServletRequest req) {

        String token;

        if (req.getCookies() != null && req.getHeader("Authorization") == null) {
            Optional<Cookie> accessToken = Arrays.stream(req.getCookies())
                                                 .filter(cookie -> cookie.getName().equals("access_token"))
                                                 .findAny();
            if (accessToken.isPresent()) {
                token = accessToken.get().getValue();
                return token;
            }
        }

        token = Optional.ofNullable(req.getHeader("Authorization"))
                        .filter(s -> s.length() > BEARER.length() && s.startsWith(BEARER))
                        .map(s -> s.substring(BEARER.length(), s.length()))
                        .orElseThrow(() -> new AuthenticationException("No JWT token found in request headers"));

        return token;
    }

    /**
     * Return UserId from token and also validate token based on secretKey.
     */
    public static String getUserId(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }
  
    /**
     * Return refreshTokenDetails and also validate token based on secretKey.
     */
    public static RefreshTokenDetails getRefreshTokenDetails(String token) {
        final Claims claims = getAllClaimsFromRefreshToken(token);
        RefreshTokenDetails refreshTokenDetails = new RefreshTokenDetails(getRefreshTokenId(claims.getSubject(), claims.getId()),
                                                                          token,
                                                                          Integer.valueOf(claims.getSubject()));
        return refreshTokenDetails;
    }
    
    /**
     * Return RefreshTokenId (UserId_UUID)
     */
    private static  String getRefreshTokenId(String userId, String Id) {
        return userId +"_" + Id;
    }
    
    public static String getUUIDFromRefreshTokenId(RefreshTokenDetails refreshTokenDetails) {
       return StringUtils.removeStart(refreshTokenDetails.getRefreshTokenId(), refreshTokenDetails.getUserId() + "_");
    }
    
    /**
     * Validate refresh token and return claims (Issuer, Subject, Audience , IssuedAt) associated with token.
     * If token is expired or invalid then throw {AuthenticationException} exception.
     */
    private static Claims getAllClaimsFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(refreshSecretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (IllegalArgumentException | JwtException ex) {
            throw new AuthenticationException("Expired or invalid Refresh token");
        }
    }

    /**
     * Validate token and return claims (Issuer, Subject, Audience , IssuedAt) associated with token.
     * If token is expired or invalid then throw {AuthenticationException} exception.
     */

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

    /**
     *  Check expiration of generated token
     *  if token is valid then return true otherwise false.
     */
    public static boolean checkExpiredJwt(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (SignatureException | ExpiredJwtException jwtException) {
            // "JWT Token is expired or Old Sessions stick in reboot"
            return true;
        }
        return false;
    }
    
    /**
     *  Check expiration of refresh token
     *  if token is valid then return true otherwise false.
     */
    public static boolean checkRefreshTokenExpiredJwt(String token) {
        try {
            Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
        } catch (SignatureException | ExpiredJwtException jwtException) {
            // "JWT Token is expired or Old Sessions stick in reboot"
            return true;
        }
        return false;
    }
    /**
     * get refresh token from cookies 
     */
    public static String getTokenFromCookies (HttpServletRequest request) {
    String refreshToken = null;
        if (request.getCookies() != null) {
            Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                                                        .filter(cookie -> cookie.getName().equals("refresh_token"))
                                                        .findAny();
            if (refreshTokenCookie.isPresent()) {
                refreshToken = refreshTokenCookie.get().getValue();
            }
        }
        return refreshToken;
    }
    /**
     * Remove cookies 
     */
    public static void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    public static void removeCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            removeCookie(response, cookie.getName());
        }
    }

}