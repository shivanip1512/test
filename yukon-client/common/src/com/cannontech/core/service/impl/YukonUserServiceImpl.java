package com.cannontech.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.YukonUserService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserServiceImpl implements YukonUserService {
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthenticationService authenticationService;

    @Override
    @Transactional
    public void saveAndSetPassword(LiteYukonUser user, String newPassword) {
        yukonUserDao.save(user);
        authenticationService.setPassword(user, newPassword);
    }
}
