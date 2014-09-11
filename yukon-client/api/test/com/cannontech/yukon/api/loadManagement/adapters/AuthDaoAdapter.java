package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AuthDaoAdapter implements AuthDao {

    @Override
    public TimeZone getUserTimeZone(LiteYukonUser user) throws BadConfigurationException, IllegalArgumentException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isAdminUser(LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean userHasAccessPAO(LiteYukonUser user, int paoID) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getFirstNotificationPin(LiteContact contact) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LiteYukonUser voiceLogin(int contactid, String pin) {
        throw new UnsupportedOperationException("not implemented");
    }

}
