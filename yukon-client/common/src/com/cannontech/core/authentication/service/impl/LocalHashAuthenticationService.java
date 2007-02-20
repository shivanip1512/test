package com.cannontech.core.authentication.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordChangeProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashAuthenticationService implements AuthenticationProvider, PasswordChangeProvider, PasswordSetProvider {
    private YukonUserPasswordDao yukonUserPasswordDao;
    private PasswordHasher passwordHasher;
    
    public LocalHashAuthenticationService() {
    }
    
    public boolean login(LiteYukonUser user, String password) {
        String hashedPassword = passwordHasher.hashPassword(password);
        boolean matches = yukonUserPasswordDao.checkPassword(user, hashedPassword);
        return matches;
    }
    

    public void changePassword(LiteYukonUser user, String oldPassword, String newPassword) throws BadAuthenticationException {
        String oldHash = passwordHasher.hashPassword(oldPassword);
        String newHash = passwordHasher.hashPassword(newPassword);
        boolean success = yukonUserPasswordDao.changePassword(user, oldHash, newHash);
        if (!success) {
            throw new BadAuthenticationException();
        }
    }

    public void setPassword(LiteYukonUser user, String newPassword) {
        String newHash = passwordHasher.hashPassword(newPassword);
        yukonUserPasswordDao.changePassword(user, newHash);
    }

    @Required
    public void setYukonUserPasswordDao(YukonUserPasswordDao yukonUserPasswordDao) {
        this.yukonUserPasswordDao = yukonUserPasswordDao;
    }

    public void setPasswordHasher(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public YukonUserPasswordDao getYukonUserPasswordDao() {
        return yukonUserPasswordDao;
    }

}
