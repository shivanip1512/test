package com.cannontech.core.authentication.service.impl;

import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.database.data.lite.LiteYukonUser;

final class MockYukonUserPasswordDao implements YukonUserPasswordDao {
    private String currentPassword;

    public MockYukonUserPasswordDao(String initialPassword) {
        currentPassword = initialPassword;
    }

    @Override
    public boolean checkPassword(LiteYukonUser user, String password) {
        return password.equals(currentPassword);
    }

    @Override
    public boolean setPassword(LiteYukonUser user, AuthType authType, String newPassword) {
        currentPassword = newPassword;
        return true;
    }
}
