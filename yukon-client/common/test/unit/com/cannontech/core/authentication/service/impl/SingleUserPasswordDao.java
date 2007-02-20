package com.cannontech.core.authentication.service.impl;

import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.database.data.lite.LiteYukonUser;

final class SingleUserPasswordDao implements YukonUserPasswordDao {
    private String currentPassword;
    
    public SingleUserPasswordDao(String initialPassword) {
        currentPassword = initialPassword;
    }

    public boolean checkPassword(LiteYukonUser user, String password) {
        return password.equals(currentPassword);
    }

    public boolean changePassword(LiteYukonUser user, String newPassword) {
        currentPassword = newPassword;
        return true;
    }

    public boolean changePassword(LiteYukonUser user, String oldPassword, String newPassword) {
        if (!currentPassword.equals(oldPassword)) {
            return false;
        }
        currentPassword = newPassword;
        return true;
    }

    public String recoverPassword(LiteYukonUser user) {
        return currentPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}