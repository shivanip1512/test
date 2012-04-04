package com.cannontech.core.authentication.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationThrottleHelper;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AuthenticationServiceImpl implements AuthenticationService, InitializingBean  {
    private final static Logger log = YukonLogManager.getLogger(AuthenticationServiceImpl.class);
    private Map<AuthType, AuthenticationProvider> providerMap;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private AuthenticationThrottleHelper authenticationThrottleHelper;

    @Override
    public AuthType getCurrentAuthType() {
        // Update to the current authentication type when password is changed.
        AuthType authType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE,
                                                                 AuthType.class, null);
        return authType;
    }

    @Override
    public synchronized LiteYukonUser login(String username, String password) throws BadAuthenticationException {
        // see if login attempt allowed and track the attempt
        authenticationThrottleHelper.loginAttempted(username);

        // find user in database
        LiteYukonUser liteYukonUser = yukonUserDao.findUserByUsername(username);
        if (liteYukonUser == null) {
            log.info("Authentication failed (unknown user): username=" + username);
            throw new BadAuthenticationException();
        }

        // ensure that user is enabled
        if (liteYukonUser.getLoginStatus().isDisabled()) {
            log.info("Authentication failed (disabled): username=" + username + ", id=" + 
                     liteYukonUser.getUserID() + ", status=" + liteYukonUser.getLoginStatus());
            throw new BadAuthenticationException();
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
            throw new BadAuthenticationException();            
        }
    }

    private AuthenticationProvider getProvder(LiteYukonUser user) {
        AuthenticationProvider provider = providerMap.get(user.getAuthType());
        if (provider == null) {
            throw new RuntimeException("Unknown AuthType: userid=" + user.getUserID() + ", authtype=" + user.getAuthType());
        }
        return provider;
    }

    @Override
    public void setPassword(LiteYukonUser yukonUser, String newPassword) {
        // Update to the current authentication type when password is changed.
        AuthType authType = getCurrentAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        PasswordSetProvider provider = (PasswordSetProvider) providerMap.get(authType);
        provider.setPassword(yukonUser, newPassword);
    }

    @Override
    public boolean supportsPasswordSet(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordSetProvider;
    }

    @Override
    public AuthenticationThrottleDto getAuthenticationThrottleData(
            String username) {
        AuthenticationThrottleDto authThrottleDto = authenticationThrottleHelper.getAuthenticationThrottleData(username);
        return authThrottleDto;
    }

    @Override
    public void removeAuthenticationThrottle(String username) {
        authenticationThrottleHelper.removeAuthenticationThrottle(username);
    }

    @Required
    public void setProviderMap(Map<AuthType, AuthenticationProvider> providerMap) {
        this.providerMap = providerMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigurationSource configSource = MasterConfigHelper.getConfiguration();
        String authThrottleExpBaseStr = configSource.getString(AuthenticationThrottleHelper.AUTH_THROTTLE_EXP_BASE_KEY);
        String authThrottleDeltaStr = configSource.getString(AuthenticationThrottleHelper.AUTH_THROTTLE_DELTA_KEY);

        double authThrottleExpBase = 0.0;
        double authThrottleDelta = 0.0;
        if (StringUtils.isNotBlank(authThrottleExpBaseStr)) {
            try {
                authThrottleExpBase = Math.abs(Double.parseDouble(authThrottleExpBaseStr.trim()));
            } catch (NumberFormatException e) {
                // use defaults
            }
        }
        if (StringUtils.isNotBlank(authThrottleDeltaStr)) {
            try {
                authThrottleDelta = Math.abs(Double.parseDouble(authThrottleDeltaStr.trim()));
            } catch (NumberFormatException e) {
                // use defaults
            }
        }
        // Use if correct values are provided, else use defaults
        if (authThrottleExpBase > 1.0 || authThrottleDelta > 0.0) {
            authenticationThrottleHelper.setAuthThrottleExpBase(authThrottleExpBase);
            authenticationThrottleHelper.setAuthThrottleDelta(authThrottleDelta);
        }
    }
}
