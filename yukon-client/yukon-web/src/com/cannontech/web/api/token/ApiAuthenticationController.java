package com.cannontech.web.api.token;

<<<<<<< HEAD
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
=======
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
>>>>>>> refs/remotes/origin/feature/React-Integration
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.login.LoginCookieHelper;

@RestController
public class ApiAuthenticationController {

    private final Logger log = YukonLogManager.getLogger(ApiAuthenticationController.class);

    @Autowired private AuthenticationService authenticationService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
<<<<<<< HEAD
    @Autowired private LoginCookieHelper loginCookieHelper;
    
=======
>>>>>>> refs/remotes/origin/feature/React-Integration
    private ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void removeExpiredRefreshTokenFromCache() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, String> entry : tokenCache.entrySet()) {
                if (TokenHelper.checkRefreshTokenExpiredJwt(entry.getValue())) {
                    tokenCache.remove(entry.getKey());
                }
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public ResponseEntity<TokenResponse> generateToken(HttpServletRequest request,HttpServletResponse resp, @RequestBody TokenRequest tokenRequest) {
=======
    public ResponseEntity<TokenResponse> generateToken(HttpServletRequest request, @RequestBody TokenRequest tokenRequest) {
>>>>>>> refs/remotes/origin/feature/React-Integration

        if (tokenRequest.getUsername() != null && tokenRequest.getPassword() != null) {
            try {
                TokenResponse response = new TokenResponse();
                LiteYukonUser user = authenticationService.login(tokenRequest.getUsername(), tokenRequest.getPassword());
                response = TokenHelper.setTokenTypeAndExpiresIn(response);
                String accessToken = TokenHelper.createToken(user.getUserID());
                response.setAccessToken(accessToken);

                RefreshTokenDetails refreshTokenDetails = TokenHelper.createRefreshToken(user.getUserID());
                response.setRefreshToken(refreshTokenDetails.getRefreshToken());

                tokenCache.put(refreshTokenDetails.getRefreshTokenId(), response.getRefreshToken());
<<<<<<< HEAD
                loginCookieHelper.setTokensInCookie(request, resp, response.getAccessToken(), response.getRefreshToken());
=======
>>>>>>> refs/remotes/origin/feature/React-Integration

                log.info("User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
                return new ResponseEntity<TokenResponse>(response, HttpStatus.OK);
            } catch (BadAuthenticationException | PasswordExpiredException e) {
                log.error(e);
                throw new AuthenticationException("Authentication Failed. Username or Password not valid.");
            }
        } else {
            throw new AuthenticationException("Username or Password not provided");
        }
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public ResponseEntity<Object> generateRefreshToken(HttpServletRequest request, HttpServletResponse resp, @RequestBody RefreshTokenRequest tokenRequest) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                                                        .filter(cookie -> cookie.getName().equals("refresh_token"))
                                                        .findAny();
            if (refreshTokenCookie.isPresent()) {
                refreshToken = refreshTokenCookie.get().getValue();
            }
        }
        if (refreshToken == null) {
            refreshToken = tokenRequest.getRefreshToken();
        }
        
        if (refreshToken != null) {
            RefreshTokenDetails refreshTokenDetails = TokenHelper.getRefreshTokenDetails(refreshToken);
            String cacheRefreshToken = tokenCache.get(refreshTokenDetails.getRefreshTokenId());

            TokenResponse response = new TokenResponse();
            if (cacheRefreshToken != null) {
                if (cacheRefreshToken.equals(refreshToken)) {
                    response = TokenHelper.setTokenTypeAndExpiresIn(response);
                    String newAccessToken = TokenHelper.createToken(Integer.valueOf(refreshTokenDetails.getUserId()));
                    String uuid = TokenHelper.getUUIDFromRefreshTokenId(refreshTokenDetails);
                    RefreshTokenDetails newRefreshTokenDetails = TokenHelper.createRefreshTokenWithUUID(Integer.valueOf(refreshTokenDetails.getUserId()),
                                                                                                        uuid);
                    response.setAccessToken(newAccessToken);
                    response.setRefreshToken(newRefreshTokenDetails.getRefreshToken());
                    // Update latest refresh token in cache
                    tokenCache.put(newRefreshTokenDetails.getRefreshTokenId(), response.getRefreshToken());
                    loginCookieHelper.setTokensInCookie(request, resp, response.getAccessToken(), response.getRefreshToken());
=======
    public ResponseEntity<Object> generateRefreshToken(HttpServletRequest request, @RequestBody RefreshTokenRequest tokenRequest) {

        if (tokenRequest.getRefreshToken() != null) {
            String refreshToken = tokenRequest.getRefreshToken();
            RefreshTokenDetails refreshTokenDetails = TokenHelper.getRefreshTokenDetails(refreshToken);
            String cacheRefreshToken = tokenCache.get(refreshTokenDetails.getRefreshTokenId());

            TokenResponse response = new TokenResponse();
            if (cacheRefreshToken != null) {
                if (cacheRefreshToken.equals(refreshToken)) {
                    response = TokenHelper.setTokenTypeAndExpiresIn(response);
                    String newAccessToken = TokenHelper.createToken(Integer.valueOf(refreshTokenDetails.getUserId()));
                    String uuid = TokenHelper.getUUIDFromRefreshTokenId(refreshTokenDetails);
                    RefreshTokenDetails newRefreshTokenDetails = TokenHelper.createRefreshTokenWithUUID(Integer.valueOf(refreshTokenDetails.getUserId()),
                                                                                                        uuid);
                    response.setAccessToken(newAccessToken);
                    response.setRefreshToken(newRefreshTokenDetails.getRefreshToken());
                    // Update latest refresh token in cache
                    tokenCache.put(newRefreshTokenDetails.getRefreshTokenId(), newRefreshTokenDetails.getRefreshToken());
>>>>>>> refs/remotes/origin/feature/React-Integration

                } else {
                    // Delete refresh token from cache
                    tokenCache.remove(refreshTokenDetails.getRefreshTokenId());
                    throw new AuthenticationException("Refresh token only valid for one-time use");
                }

            } else {
                throw new AuthenticationException("Refresh token not valid.");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            throw new AuthenticationException("Refresh token not provided");
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(HttpServletRequest request, @RequestBody LogoutRequest logoutRequest) {

        if (logoutRequest.getRefreshToken() != null) {
            try {
                LogoutResponse logoutResponse = new LogoutResponse();
                String refreshToken = logoutRequest.getRefreshToken();
                RefreshTokenDetails refreshTokenDetails = TokenHelper.getRefreshTokenDetails(refreshToken);
                String cacheRefreshToken = tokenCache.get(refreshTokenDetails.getRefreshTokenId());

                if (cacheRefreshToken.equals(refreshToken)) {
                    // Remove refreshtoken from cache
                    if (logoutRequest.isLogoutfromAllSystem()) {
                        List<String> refreshTokenIds = tokenCache.keySet()
                                                                 .stream()
                                                                 .filter(refreshTokenId -> refreshTokenId.startsWith(refreshTokenDetails.getUserId() + "_"))
                                                                 .collect(Collectors.toList());
                        refreshTokenIds.forEach(refreshTokenId -> {
                            tokenCache.remove(refreshTokenId);
                        });
                    } else {
                        tokenCache.remove(refreshTokenDetails.getRefreshTokenId());
                    }
                } else {
                    throw new AuthenticationException("Refresh token not valid.");
                }
                logoutResponse.setLogout(true);
                return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
            } catch (Exception e) {
                throw new AuthenticationException("Expired or invalid Refresh token.");
            }
        } else {
            throw new AuthenticationException("Refresh token not provided");
        }

    }

}