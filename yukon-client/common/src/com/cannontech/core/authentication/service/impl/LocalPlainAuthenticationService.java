package com.cannontech.core.authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalPlainAuthenticationService implements AuthenticationProvider, PasswordSetProvider {
    @Autowired protected YukonUserPasswordDao yukonUserPasswordDao;

    @Override
    public boolean login(LiteYukonUser user, String password) {
        return yukonUserPasswordDao.checkPassword(user, password);
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword) {
        yukonUserPasswordDao.setPassword(user, AuthType.PLAIN, newPassword);
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousPassword) {
        return newPassword.equals(previousPassword);
    }

    public void setYukonUserPasswordDao(YukonUserPasswordDao yukonUserPasswordDao) {
        this.yukonUserPasswordDao = yukonUserPasswordDao;
    }
}
