package com.cannontech.core.authentication.service.impl;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashV2AuthenticationService implements AuthenticationProvider, PasswordSetProvider{
    private final StrongPasswordEncryptor digester = new StrongPasswordEncryptor();

    @Autowired private YukonUserPasswordDao yukonUserPasswordDao;
    @Autowired private UsersEventLogService usersEventLogService; 

    @Override
    public boolean login(LiteYukonUser user, String password) {
        String digest = yukonUserPasswordDao.getDigest(user);
        return digester.checkPassword(password, digest);
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword, LiteYukonUser createdBy) {
        String digest = digester.encryptPassword(newPassword);
        yukonUserPasswordDao.setPassword(user, AuthType.HASH_SHA_V2, digest);
        usersEventLogService.passwordUpdated(user, createdBy); 
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword,
        String previousDigest) {
        return digester.checkPassword(newPassword, previousDigest);
    }
}
