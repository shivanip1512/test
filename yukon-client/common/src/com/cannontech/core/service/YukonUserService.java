package com.cannontech.core.service;

import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserService {
    /**
     * Save the user to the database and update the password in a single transaction.  This is a
     * wrapper around calls to {@link YukonUserDao#save(LiteYukonUser)} and
     * {@link AuthenticationService#setPassword(LiteYukonUser, String)} in a single transaction.
     */
    void saveAndSetPassword(LiteYukonUser user, String newPassword);
}
