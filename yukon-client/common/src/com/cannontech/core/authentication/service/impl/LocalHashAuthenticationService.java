package com.cannontech.core.authentication.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This authentication service will no longer be used as of 5.5.3.  However,
 * it is important that it remain in Yukon forever as older customer logins
 * will depend on it.  (While we are doing what we can to update these older
 * logins, there is no way we will ever be able to guarantee that all
 * customers everywhere will have been updated.)
 */
public class LocalHashAuthenticationService implements AuthenticationProvider, PasswordSetProvider {
    @Autowired protected YukonUserPasswordDao yukonUserPasswordDao;
    @Autowired private UsersEventLogService usersEventLogService; 

    private final MessageDigest messageDigest;
    // the following value must never, ever, ever be changed
    private static final String salt = "88302c9d15581fd7abc6aa6742cf71b9da852d33";

    public LocalHashAuthenticationService() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("SHA");
    }

    private synchronized String hashPassword(String password) {
        messageDigest.reset();
        messageDigest.update(salt.getBytes());

        byte[] input = password.getBytes();
        byte[] raw = messageDigest.digest(input);
        String result = toHexString(raw);
        return result;
    }

    private String toHexString(final byte[] input) {
        Formatter hexFormatter = new Formatter();
        for (byte b : input) {
            hexFormatter.format("%x", b);
        }
        String retVal = hexFormatter.out().toString();
        hexFormatter.close();
        return retVal;
    }

    @Override
    public boolean login(LiteYukonUser user, String password) {
        String hashedPassword = hashPassword(password);
        String digest = yukonUserPasswordDao.getDigest(user);
        return StringUtils.equals(hashedPassword, digest);
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword, LiteYukonUser createdBy) {
        String newHash = hashPassword(newPassword);
        yukonUserPasswordDao.setPassword(user, AuthType.HASH_SHA, newHash);
        usersEventLogService.passwordUpdated(user, createdBy); 
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousDigest) {
        String newHashedPassword = hashPassword(newPassword);
        return newHashedPassword.equals(previousDigest);
    }
}
