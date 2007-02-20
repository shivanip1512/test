package com.cannontech.core.authentication.service.impl;

import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class NullAuthenticationService implements AuthenticationProvider {
    public boolean login(LiteYukonUser user, String password) {
        return false;
    }
}
