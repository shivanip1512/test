package com.cannontech.web.api.token;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.cannontech.web.login.impl.LoginCookieHelperImpl;

@RestController
public class ApiAuthenticationController {

    private final Logger log = YukonLogManager.getLogger(ApiAuthenticationController.class);

    @Autowired private AuthenticationService authenticationService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private LoginCookieHelper loginCookieHelper;

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
    public ResponseEntity<TokenResponse> generateToken(HttpServletRequest request,HttpServletResponse resp, @RequestBody TokenRequest tokenRequest) {

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
                loginCookieHelper.setTokensInCookie(request, resp, response.getAccessToken(), response.getRefreshToken());

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
    public ResponseEntity<Object> generateRefreshToken(HttpServletRequest request, HttpServletResponse resp, @RequestBody RefreshTokenRequest tokenRequest) {

        String refreshToken = TokenHelper.getTokenFromCookies(request);

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
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse resp, @RequestBody LogoutRequest logoutRequest) {

        String refreshToken = TokenHelper.getTokenFromCookies(request);

        if (refreshToken == null) {
            refreshToken = logoutRequest.getRefreshToken();
        }

        if (refreshToken != null) {
            try {
                LogoutResponse logoutResponse = new LogoutResponse();
                //String refreshToken = logoutRequest.getRefreshToken();
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
                    //remove token from cookies
                    LoginCookieHelperImpl.removeCookies(request, resp);
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