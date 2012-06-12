package com.cannontech.core.authentication.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationThrottleService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.primitives.Ints;

public class AuthenticationServiceImpl implements AuthenticationService, InitializingBean {
    private final static Logger log = YukonLogManager.getLogger(AuthenticationServiceImpl.class);
    private Map<AuthType, AuthenticationProvider> providerMap;
    
    private AuthenticationThrottleService authenticationThrottleService;
    @Autowired @Qualifier("static") private AuthenticationThrottleService staticAuthenticationThrottleService;
    @Autowired @Qualifier("increasing") private AuthenticationThrottleService increasingAuthenticationThrottleService;
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserDao yukonUserDao;

    @Override
    public AuthType getDefaultAuthType(LiteYukonUser user) {
        AuthType authType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, user);
        return authType;
    }

    @Override
    public synchronized LiteYukonUser login(String username, String password) throws BadAuthenticationException {
        // see if login attempt allowed and track the attempt
        authenticationThrottleService.loginAttempted(username);

        // find user in database
        LiteYukonUser liteYukonUser = yukonUserDao.findUserByUsername(username);
        if (liteYukonUser == null) {
            log.info("Authentication failed (unknown user): username=" + username);
            throw new BadAuthenticationException();
        }

        // ensure that user is enabled
        if (liteYukonUser.getLoginStatus().isDisabled()) {
            log.info("Authentication failed (disabled): username=" + username + ", id=" + liteYukonUser.getUserID() + ", status=" + liteYukonUser.getLoginStatus());
            throw new BadAuthenticationException();
        }

        AuthenticationProvider provider = getProvder(liteYukonUser);

        // attempt login; remove auth throttle if login successful
        if (provider.login(liteYukonUser, password)) {
            log.debug("Authentication succeeded: username=" + username);
            authenticationThrottleService.loginSucceeded(username);
            return liteYukonUser;
            
        } else {
            // login must have failed
            log.info("Authentication failed (auth failed): username=" + username + ", id=" + liteYukonUser.getUserID());
            throw new BadAuthenticationException();
        }
    }

    public boolean isPasswordExpired(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.findPasswordPolicy(user);
        if (user.isForceReset() || 
            (passwordPolicy != null && passwordPolicy.getMaxPasswordAge().isShorterThan(passwordPolicy.getPasswordAge(user)))) {
            return true;
        }
        
        return false;
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
        AuthType authType = getDefaultAuthType(yukonUser);
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        PasswordSetProvider provider = (PasswordSetProvider) providerMap.get(authType);
        provider.setPassword(yukonUser, newPassword);
    }

    @Override
    public boolean isPasswordBeingReused(LiteYukonUser yukonUser, String newPassword) {

        // Update to the current authentication type when password is changed.
        AuthType authType = getDefaultAuthType(yukonUser);
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        List<PasswordHistory> passwordHistories = passwordHistoryDao.getPasswordHistory(yukonUser.getUserID());
        PasswordPolicy passwordPolicy = passwordPolicyService.findPasswordPolicy(yukonUser);
        
        // Check the passwords to see if any of them are attempting to be reused
        int numberOfPasswordToCheck = Ints.min(passwordHistories.size(), passwordPolicy.getPasswordHistory());
        for (int i = 0; i < numberOfPasswordToCheck ; i++) {
            PasswordHistory passwordHistory = passwordHistories.get(i);
            String previousPassword = passwordHistory.getPassword(); 
            AuthType passwordAuthType = passwordHistory.getAuthType();
            
            // Uses the old authtype to check if the submitted password is being reused.
            PasswordSetProvider provider = (PasswordSetProvider) providerMap.get(passwordAuthType);
            boolean isPasswordBeingReused = provider.comparePassword(yukonUser, newPassword, previousPassword);
            if (isPasswordBeingReused){
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void expireAllPasswords(int groupId) {
        yukonUserDao.updateForceResetByGroupId(groupId, true);
    }
    
    @Override
    public boolean supportsPasswordSet(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordSetProvider;
    }

    @Override
    public AuthenticationThrottleDto getAuthenticationThrottleData(String username) {
        AuthenticationThrottleDto authThrottleDto = authenticationThrottleService.getAuthenticationThrottleData(username);
        return authThrottleDto;
    }

    @Override
    public void removeAuthenticationThrottle(String username) {
        authenticationThrottleService.removeAuthenticationThrottle(username);
    }

    @Required
    public void setProviderMap(Map<AuthType, AuthenticationProvider> providerMap) {
        this.providerMap = providerMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String authenticationTimeoutStyleEnumStr = configurationSource.getString(MasterConfigStringKeysEnum.AUTHENTICATION_TIMEOUT_STYLE, "STATIC");
        AuthenticationTimeoutStyleEnum authenticationTimeoutStyle = AuthenticationTimeoutStyleEnum.valueOf(authenticationTimeoutStyleEnumStr);
        
        switch (authenticationTimeoutStyle) {
            case STATIC:
                this.authenticationThrottleService = staticAuthenticationThrottleService;
                break;
            case INCREMENTAL:
                this.authenticationThrottleService = increasingAuthenticationThrottleService;
                break;
        }
    }

    enum AuthenticationTimeoutStyleEnum {
        STATIC,
        INCREMENTAL,
        ;
    }
}
