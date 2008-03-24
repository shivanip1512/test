package com.cannontech.web.login.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.web.login.LoginRequestHandler;
import com.cannontech.web.login.LoginService;

public class BGESiteMinderLoginRequestHandler implements LoginRequestHandler {
    private static final String HASH_ALGORITHM = "MD5";
    private static final String HEADER_LOGIN_TOKEN = "LOGIN_TOKEN";
    private static final String SECRET_PROPERTY_KEY = "BGE_SECRET";
    private ConfigurationSource config;
    private LoginService loginService;

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean handleLoginRequest(final HttpServletRequest request) throws IOException, ServletException {
        String loginToken = request.getHeader(HEADER_LOGIN_TOKEN);
        if (loginToken == null) return false;

        String premiseNumber = ServletRequestUtils.getRequiredStringParameter(request, "PremiseNumber");
        String accountIdentifier = ServletRequestUtils.getRequiredStringParameter(request, "Account_Identifier");
        
        String secret = config.getString(SECRET_PROPERTY_KEY);
        if (secret.equals("")) return false;
        
        try {
            String input = premiseNumber + loginToken + secret;
            String hash = hash(input);
            
            if (!hash.equals(accountIdentifier)) return false;
            
            String username = premiseNumber;
            String password = premiseNumber;
            
            boolean success = loginService.login(request, username, password);
            return success;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private String hash(final String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] raw = md.digest(input.getBytes());
        String hash = toHexString(raw);
        return hash;
    }
    
    private String toHexString(final byte[] raw) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : raw) {
            int andResult = 0xFF & b;
            String toHex = String.format("%02x", andResult);
            sb.append(toHex);
        }
        String hexString = sb.toString();
        return hexString;
    }
    
    public void setConfig(ConfigurationSource config) {
        this.config = config;
    }

    
}
