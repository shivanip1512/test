package com.cannontech.core.authentication.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

public class AuthenticationServiceImpl implements AuthenticationService, InitializingBean {
    private final static Logger log = YukonLogManager.getLogger(AuthenticationServiceImpl.class);
    private Map<AuthType, AuthenticationProvider> providerMap;
    
    private AuthenticationThrottleService authenticationThrottleService;
    @Autowired @Qualifier("static") private AuthenticationThrottleService staticAuthenticationThrottleService;
    @Autowired @Qualifier("increasing") private AuthenticationThrottleService increasingAuthenticationThrottleService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private YukonSettingsDao yukonSettingsDao;
    @Autowired private YukonUserDao yukonUserDao;

    @Override
    public AuthType getDefaultAuthType() {
        AuthenticationCategory authenticationCategory = getDefaultAuthenticationCategory();
        return authenticationCategory.getSupportingAuthType();
    }

    @Override
    public AuthenticationCategory getDefaultAuthenticationCategory() {
        AuthenticationCategory authenticationCategory =
                yukonSettingsDao.getSettingEnumValue(YukonSetting.DEFAULT_AUTH_TYPE, AuthenticationCategory.class);
        return authenticationCategory;
    }

    @Override
    public synchronized LiteYukonUser login(String username, String password) throws BadAuthenticationException, PasswordExpiredException {
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
            
            // Check to see if the user's password is expired.
            boolean passwordExpired = isPasswordExpired(liteYukonUser);
            if (passwordExpired) {
                throw new PasswordExpiredException("The user's password is expired.  Please login to the web interface to reset it. ("+liteYukonUser.getUsername()+")" );
            }

            return liteYukonUser;
            
        } else {
            // login must have failed
            log.info("Authentication failed (auth failed): username=" + username + ", id=" + liteYukonUser.getUserID());
            throw new BadAuthenticationException();
        }
    }

    @Override
    public boolean isPasswordExpired(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);
        if (user.isForceReset() || 
            (passwordPolicy != null &&
             passwordPolicy.getMaxPasswordAge() != Duration.ZERO && 
             passwordPolicy.getMaxPasswordAge().isShorterThan(passwordPolicy.getPasswordAge(user)))) {
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
        AuthType authType = getDefaultAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        PasswordSetProvider provider = (PasswordSetProvider) providerMap.get(authType);
        provider.setPassword(yukonUser, newPassword);
    }

    @Override
    public boolean isPasswordBeingReused(LiteYukonUser yukonUser, String newPassword, int numberOfPasswordsToCheck) {
        if (yukonUser == null) {
            return false;
        }
        
        // Update to the current authentication type when password is changed.
        AuthType authType = getDefaultAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        // Check the passwords to see if any of them are attempting to be reused
        List<PasswordHistory> passwordHistories = passwordHistoryDao.getPasswordHistory(yukonUser.getUserID());
        for (int i = 0; i < numberOfPasswordsToCheck && i < passwordHistories.size(); i++) {
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
