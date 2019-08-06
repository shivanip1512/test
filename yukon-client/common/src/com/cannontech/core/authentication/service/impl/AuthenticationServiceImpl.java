package com.cannontech.core.authentication.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleService;
import com.cannontech.core.authentication.service.PasswordEncrypter;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final static Logger log = YukonLogManager.getLogger(AuthenticationServiceImpl.class);
    private Map<AuthType, AuthenticationProvider> providerMap;
    
    private AuthenticationThrottleService authenticationThrottleService;
    @Autowired @Qualifier("static") private AuthenticationThrottleService staticAuthenticationThrottleService;
    @Autowired @Qualifier("increasing") private AuthenticationThrottleService increasingAuthenticationThrottleService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserPasswordDao yukonUserPasswordDao;
    @Autowired private UsersEventLogService usersEventLogService; 

    @Override
    public AuthType getDefaultAuthType() {
        AuthenticationCategory authenticationCategory = getDefaultAuthenticationCategory();
        return authenticationCategory.getSupportingAuthType();
    }

    @Override
    public AuthenticationCategory getDefaultAuthenticationCategory() {
        AuthenticationCategory authenticationCategory =
                globalSettingDao.getEnum(GlobalSettingType.DEFAULT_AUTH_TYPE, AuthenticationCategory.class);
        return authenticationCategory;
    }

    @Override
    public synchronized LiteYukonUser login(String username, String password) throws BadAuthenticationException,
            PasswordExpiredException {
        boolean isPasswordMatchedWithOldAuthType = false;
        boolean isPasswordMatched = false;
     // see if login attempt allowed and track the attempt
        authenticationThrottleService.loginAttempted(username);

        // find user in database
        LiteYukonUser user = yukonUserDao.findUserByUsername(username);
        if (user == null) {
            log.info("Authentication failed (unknown user): username=" + username);
            throw new BadAuthenticationException(BadAuthenticationException.Type.UNKNOWN_USER);
        }

        // ensure that user is enabled
        if (user.getLoginStatus().isDisabled()) {
            log.info("Authentication failed (disabled): username=" + username + ", id=" + user.getUserID() +
                ", status=" + user.getLoginStatus());
            throw new BadAuthenticationException(BadAuthenticationException.Type.DISABLED_USER);
        }
        
        UserAuthenticationInfo authenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        AuthType authType = authenticationInfo.getAuthType();
        
        /*
         * 
         * If the authentication type is plain the following if loop is  excuted 
         * so the the "PLAIN" Password type is implemented 
         * 
         * 
         */
        
        if (authType == AuthType.PLAIN) {
            // This is an old plain text password.   Service manager will eventually encrypt this if we leave it
            // alone unless someone updated the database manually like this (which we want to support):
            //   UPDATE yukonUser SET password = 'myNewPassword', authType = 'PLAIN' where username = 'me'
            encryptPlainTextPassword(user);
            authenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
            authType = authenticationInfo.getAuthType();
            
        }
        /*
         * At this point we get all the supporting Authentication
         */
        AuthType supportingAuthType = AuthenticationCategory.getByAuthType(authType).getSupportingAuthType();
        if (authType != supportingAuthType) {
            isPasswordMatchedWithOldAuthType = verifyPasswordWithOldAuthType(user, password, authType);
            checkExpirationAndAuthentication(isPasswordMatchedWithOldAuthType, user, username);
        }
        AuthenticationProvider provider = providerMap.get(supportingAuthType);
        if (provider == null) {
            throw new RuntimeException("Unknown AuthType: userid=" + user.getUserID() + ", authtype="
                + supportingAuthType);
        }
        if (isPasswordMatchedWithOldAuthType) {
            PasswordEncrypter passwordEncrypter = (PasswordEncrypter) providerMap.get(supportingAuthType);
            String newDigest = passwordEncrypter.encryptPassword(password);
            yukonUserPasswordDao.setPassword(user, supportingAuthType, newDigest);
            usersEventLogService.passwordUpdated(user, user); 
        } else {
            isPasswordMatched = provider.login(user, password);
            checkExpirationAndAuthentication(isPasswordMatched, user, username);
        }
        return user;
    }

    /*
     * Method is used for checking password expiration and also authenticating the user.
     * @param isPasswordMatched - this Parameter is used for authentication.
     * @throws BadAuthenticationException - This exception will be thrown if authentication fails.
     * @throws PasswordExpiredException - This exception will be thrown if password has expired.
     */
    private void checkExpirationAndAuthentication(boolean isPasswordMatched, LiteYukonUser user, String username)
            throws BadAuthenticationException, PasswordExpiredException {
        if (!isPasswordMatched) {
            // login must have failed
            log.info("Authentication failed (auth failed): username=" + username + ", id=" + user.getUserID());
            throw new BadAuthenticationException(BadAuthenticationException.Type.INVALID_PASSWORD);
        } else {
            log.debug("Authentication succeeded: username=" + username);
            authenticationThrottleService.loginSucceeded(username);
            // Check to see if the user's password is expired.
            boolean passwordExpired = isPasswordExpired(user);
            if (passwordExpired) {
                throw new PasswordExpiredException("The user's password is expired.  Please login to the web "
                        + "interface to reset it. (" + user.getUsername() + ")");
            }
        }
    }

    @Override
    public boolean isPasswordExpired(LiteYukonUser user) {
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        AuthType supportingAuthType = userAuthenticationInfo.getAuthenticationCategory().getSupportingAuthType();
        // If the user's authtype doesn't support setting passwords, password expiration is not a thing so don't check it
        if (supportsPasswordSet(supportingAuthType)) {
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);
            if (user.isForceReset() || 
                (passwordPolicy != null &&
                 passwordPolicy.getMaxPasswordAge() != Duration.ZERO && 
                 passwordPolicy.getMaxPasswordAge().isShorterThan(passwordPolicy.getPasswordAge(userAuthenticationInfo)))) {
                return true;
            }
        }
        else {
            log.debug("Password Expiration not supported for " + supportingAuthType + " authentication method");
        }
        return false;
    }

    /**
     * This method checks to see if the user's login will expire in a certain amount of time in the future.
     * This checks the login's password age against the password policy's allowed age.
     */
    @Override
    public boolean doesPasswordExpireInDays(LiteYukonUser user, int numberOfDays) {
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        AuthType authType = userAuthenticationInfo.getAuthType();
        // Does the AuthType support setting passwords
        if (supportsPasswordSet(authType)) {
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);
            if (passwordPolicy != null && passwordPolicy.getMaxPasswordAge() != Duration.ZERO) {
                Duration warningAge = passwordPolicy.getPasswordAge(userAuthenticationInfo).plus(Duration.standardDays(numberOfDays));
                return passwordPolicy.getMaxPasswordAge().isShorterThan(warningAge);
            }
        }
        return false;
    }

    @Override
    public void setAuthenticationCategory(LiteYukonUser user, AuthenticationCategory authenticationCategory) {
        AuthType authType = authenticationCategory.getSupportingAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (supportsSetPassword) {
            throw new UnsupportedOperationException("A password is required for authentication type: " + authType);
        }

        yukonUserPasswordDao.setAuthType(user, authType);
    }

    @Override
    public void setPassword(LiteYukonUser user, AuthenticationCategory authenticationCategory, String newPassword, LiteYukonUser changedByUser) {
        AuthType authType = authenticationCategory.getSupportingAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        PasswordSetProvider provider = (PasswordSetProvider) providerMap.get(authType);
        provider.setPassword(user, newPassword, changedByUser);
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword, LiteYukonUser changedByUser) {
        UserAuthenticationInfo authenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        setPassword(user, authenticationInfo.getAuthenticationCategory(), newPassword, changedByUser);
    }
    
    @Override
    public void setPasswordWithDefaultAuthCat(LiteYukonUser user, String newPassword, LiteYukonUser changedByUser) {
        // Update to the current authentication type when password is changed.
        AuthenticationCategory authenticationCategory = getDefaultAuthenticationCategory();
        setPassword(user, authenticationCategory, newPassword, changedByUser);
    }

    @Override
    @Transactional
    public boolean isPasswordBeingReused(LiteYukonUser user, String newPassword, int numberOfPasswordsToCheck) {
        if (user == null) {
            return false;
        }

        // Verify we can change the password.
        AuthType authType = yukonUserDao.getUserAuthenticationInfo(user.getUserID()).getAuthType();
        boolean supportsSetPassword = supportsPasswordSet(authType);
        if (!supportsSetPassword) {
            throw new UnsupportedOperationException("setPassword not supported for type: " + authType);
        }

        // Do the check to see if this password has been used recently.
        List<PasswordHistory> passwordHistories = passwordHistoryDao.getPasswordHistory(user);
        for (int i = 0; i < numberOfPasswordsToCheck && i < passwordHistories.size(); i++) {
            PasswordHistory passwordHistory = passwordHistories.get(i);
            String historicDigest = passwordHistory.getPassword(); 
            AuthType historicAuthType = passwordHistory.getAuthType();
            if (historicAuthType == AuthType.PLAIN) {
                // Service manager will eventually encrypt this so we don't need to here.
                if (newPassword.equals(passwordHistory.getPassword())) {
                    return true;
                }
            } else {
                PasswordSetProvider historicProvider = (PasswordSetProvider) providerMap.get(historicAuthType);
                boolean passwordReused = historicProvider.comparePassword(user, newPassword, historicDigest);
                if (passwordReused) {
                    return true;
                }
            }
        }

        return false;
    }
    
    @Override
    public void expireAllPasswords(int roleGroupId) {
        yukonUserDao.updateForceResetByRoleGroupId(roleGroupId, true);
    }

    @Override
    @Transactional
    public void encryptPlainTextPassword(LiteYukonUser user) {
        String password = yukonUserPasswordDao.getDigest(user);
        AuthType encryptedAuthType = AuthenticationCategory.ENCRYPTED.getSupportingAuthType();
        PasswordEncrypter provider = (PasswordEncrypter) providerMap.get(encryptedAuthType);
        String newDigest = provider.encryptPassword(password);
        yukonUserPasswordDao.setPasswordWithoutHistory(user, encryptedAuthType, newDigest);
        usersEventLogService.passwordUpdated(user, user);
    }
    
    private boolean verifyPasswordWithOldAuthType(LiteYukonUser user, String password, AuthType historicAuthType) {
        String storePassword = yukonUserPasswordDao.getDigest(user);
        PasswordSetProvider historicProvider = (PasswordSetProvider) providerMap.get(historicAuthType);
        return historicProvider.comparePassword(user, password, storePassword);
    }

    @Override
    public boolean supportsPasswordSet(AuthenticationCategory authenticationCategory) {
        return supportsPasswordSet(authenticationCategory.getSupportingAuthType());
    }

    @Override
    public boolean supportsPasswordSet(AuthType type) {
        AuthenticationProvider provider = providerMap.get(type);
        return provider instanceof PasswordSetProvider;
    }

    @Override
    public AuthenticationThrottleDto getAuthenticationThrottleData(String username) {
        AuthenticationThrottleDto authThrottleDto =
                authenticationThrottleService.getAuthenticationThrottleData(username);
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

    @PostConstruct
    public void init() throws Exception {
        String authenticationTimeoutStyleEnumStr =
                configurationSource.getString(MasterConfigString.AUTHENTICATION_TIMEOUT_STYLE, "STATIC");
        AuthenticationTimeoutStyleEnum authenticationTimeoutStyle =
                AuthenticationTimeoutStyleEnum.valueOf(authenticationTimeoutStyleEnumStr);
        
        switch (authenticationTimeoutStyle) {
            case STATIC:
                this.authenticationThrottleService = staticAuthenticationThrottleService;
                break;
            case INCREMENTAL:
                this.authenticationThrottleService = increasingAuthenticationThrottleService;
                break;
        }
    }

    public boolean validateOldPassword(String username, String password) {
        try {
            login(username, password);
            return true;
        } catch (BadAuthenticationException e) {
            return false;
        } catch (PasswordExpiredException e) {
            return true;
        }
    }

    enum AuthenticationTimeoutStyleEnum {
        STATIC,
        INCREMENTAL,
        ;
    }

    @Override
    public void setForceResetForUser(LiteYukonUser user, YNBoolean isForceReset) {
        yukonUserPasswordDao.setForceResetForUser(user, isForceReset);
    }
}
