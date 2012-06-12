package com.cannontech.core.authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashAuthenticationService implements AuthenticationProvider, PasswordSetProvider {
    @Autowired protected YukonUserPasswordDao yukonUserPasswordDao;
    
    private PasswordHasher passwordHasher;

    @Override
    public boolean login(LiteYukonUser user, String password) {
        String hashedPassword = passwordHasher.hashPassword(password);
        boolean matches = yukonUserPasswordDao.checkPassword(user, hashedPassword);
        return matches;
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword) {
        String newHash = passwordHasher.hashPassword(newPassword);
        yukonUserPasswordDao.setPassword(user, AuthType.HASH_SHA, newHash);
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousPassword) {
        String newHashedPassword = passwordHasher.hashPassword(newPassword);
        return newHashedPassword.equals(previousPassword);
    }

    public void setPasswordHasher(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }
    
    public void setYukonUserPasswordDao(YukonUserPasswordDao yukonUserPasswordDao) {
        this.yukonUserPasswordDao = yukonUserPasswordDao;
    }
}