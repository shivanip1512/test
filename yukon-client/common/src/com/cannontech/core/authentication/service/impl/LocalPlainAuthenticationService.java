package com.cannontech.core.authentication.service.impl;

import org.apache.commons.lang.StringUtils;
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
        String digest = yukonUserPasswordDao.getDigest(user);
        return StringUtils.equals(password, digest);
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword) {
        yukonUserPasswordDao.setPassword(user, AuthType.PLAIN, newPassword);
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousDigest) {
        return newPassword.equals(previousDigest);
    }

    public void setYukonUserPasswordDao(YukonUserPasswordDao yukonUserPasswordDao) {
        this.yukonUserPasswordDao = yukonUserPasswordDao;
    }
}
