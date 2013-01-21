package com.cannontech.core.authentication.service.impl;

import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.database.data.lite.LiteYukonUser;

final class MockYukonUserPasswordDao implements YukonUserPasswordDao {
    private String digest;

    public MockYukonUserPasswordDao(String initialDigest) {
        digest = initialDigest;
    }

    @Override
    public boolean setPassword(LiteYukonUser user, AuthType authType, String newDigest) {
        digest = newDigest;
        return true;
    }

    @Override
    public String getDigest(LiteYukonUser user) throws IllegalArgumentException {
        return digest;
    }

    @Override
    public void setAuthType(LiteYukonUser user, AuthType authType) {
    }
}
