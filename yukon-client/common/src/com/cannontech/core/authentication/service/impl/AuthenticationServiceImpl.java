package com.cannontech.core.authentication.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationThrottleHelper;
import com.cannontech.core.authentication.service.PasswordChangeProvider;
import com.cannontech.core.authentication.service.PasswordRecoveryProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AuthenticationServiceImpl implements AuthenticationService  {
    private static Logger log = YukonLogManager.getLogger(AuthenticationServiceImpl.class);
    private Map<AuthType, AuthenticationProvider> providerMap;
    private YukonUserDao yukonUserDao;
    private AuthenticationThrottleHelper authenticationThrottleHelper;

    public LiteYukonUser login(String username, String password) throws BadAuthenticationException {
        // track login attempt and get the auth throttle duration seconds
        long throttleSeconds = authenticationThrottleHelper.loginAttempted(username);

        // find user in database
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(username);
        if (liteYukonUser == null) {
            log.info("Authentication failed (unknown user): username=" + username);
            throw new AuthenticationThrottleException(throttleSeconds);
        }

        // ensure that user is enabled
        if (CtiUtilities.isDisabled(liteYukonUser.getStatus())) {
            log.info("Authentication failed (disabled): username=" + username + ", id=" + 
                     liteYukonUser.getUserID() + ", status=" + liteYukonUser.getStatus());
            throw new AuthenticationThrottleException(throttleSeconds);
        }

        AuthenticationProvider provider = getProvder(liteYukonUser);

        // attempt login; remove auth throttle if login successful
        if (provider.login(liteYukonUser, password)) {
            log.debug("Authentication succeeded: username=" + username);
            authenticationThrottleHelper.loginSucceeded(username);
            return liteYukonUser;
        } else {
            // login must have failed
            log.info("Authentication failed (auth failed): username=" + username + ", id=" +
                     liteYukonUser.getUserID());
            throw new AuthenticationThrottleException(throttleSeconds);            
        }
    }

    private AuthenticationProvider getProvder(LiteYukonUser user) {
        AuthenticationProvider provider = providerMap.get(user.getAuthType());
        if (provider == null) {
            throw new RuntimeException("Unknown AuthType: userid=" + user.getUserID() + ", authtype=" + user.getAuthType());
        }
        return provider;
    }

    public void changePassword(LiteYukonUser yukonUser, String oldPassword, String newPassword) throws BadAuthenticationException {
        AuthType authType = yukonUser.getAuthType();
        boolean supportsChangePassword = supportsPasswordChange(authType);
        if (!supportsChangePassword) throw new UnsupportedOperationException("changePassword not supported for type: " + authType);
        
        AuthenticationProvider provider = getProvder(yukonUser);
        PasswordChangeProvider changeProvider = (PasswordChangeProvider) provider;
        changeProvider.changePassword(yukonUser, oldPassword, newPassword);
    }

    public String recoverPassword(LiteYukonUser yukonUser) {
        AuthType authType = yukonUser.getAuthType();
        boolean supportsRecoverPassword = supportsPasswordRecovery(authType);
        if (!supportsRecoverPassword) throw new UnsupportedOperationException("recoverPassword not supported for type: " + authType);
        
        AuthenticationProvider provider = getProvder(yukonUser);
        PasswordRecoveryProvider recoveryProvider = (PasswordRecoveryProvider) provider;
        return recoveryProvider.getPassword(yukonUser);
    }

    public void setPassword(LiteYukonUser yukonUser, String newPassword) {
        AuthType authType = yukonUser.getAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        
        AuthenticationProvider provider = getProvder(yukonUser);
        PasswordSetProvider changeProvider = (PasswordSetProvider) provider;
        changeProvider.setPassword(yukonUser, newPassword);
    }

    public boolean supportsPasswordChange(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordChangeProvider;
    }

    public boolean supportsPasswordRecovery(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordRecoveryProvider;
    }

    public boolean supportsPasswordSet(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordSetProvider;
    }

    public AuthenticationThrottleDto getAuthenticationThrottleData(
            String username) {
        AuthenticationThrottleDto authThrottleDto = authenticationThrottleHelper.getAuthenticationThrottleData(username);
        return authThrottleDto;
    }

    public void removeAuthenticationThrottle(String username) {
        authenticationThrottleHelper.removeAuthenticationThrottle(username);
    }
    
    @Required
    public void setProviderMap(Map<AuthType, AuthenticationProvider> providerMap) {
        this.providerMap = providerMap;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Required
    public void setAuthenticationThrottleHelper(
            AuthenticationThrottleHelper authenticationThrottleHelper) {
        this.authenticationThrottleHelper = authenticationThrottleHelper;
    }
}

