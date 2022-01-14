package com.cannontech.web.api.token;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.database.data.lite.LiteYukonUser;

@RestController
public class ApiAuthenticationController {
    private final Logger log = YukonLogManager.getLogger(ApiAuthenticationController.class);
    @Autowired private AuthenticationService authenticationService;

    @RequestMapping(value = "/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> generateToken(HttpServletRequest request, @RequestBody TokenRequest tokenRequest) {

        TokenResponse response = new TokenResponse();

        if (tokenRequest.getUsername() != null && tokenRequest.getPassword() != null) {
            try {
                LiteYukonUser user = authenticationService.login(tokenRequest.getUsername(), tokenRequest.getPassword());
                response = TokenHelper.setTokenTypeAndExpiresIn();
                String accessToken = TokenHelper.createToken(user.getUserID());
                response.setAccessToken(accessToken);
                String refreshToken = TokenHelper.createRefreshToken(user.getUserID());
                response.setRefreshToken(refreshToken);
                log.info("User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from "
                        + request.getRemoteAddr());
                return new ResponseEntity<TokenResponse>(response, HttpStatus.OK);
            } catch (BadAuthenticationException | PasswordExpiredException e) {
                throw new AuthenticationException("Authentication Failed. Username or Password not valid." + e);
            }

        } else {
            throw new AuthenticationException("Username or Password not provided");
        }
    }

}
