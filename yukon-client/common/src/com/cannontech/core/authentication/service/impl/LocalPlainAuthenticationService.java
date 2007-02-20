package com.cannontech.core.authentication.service.impl;

import com.cannontech.core.authentication.service.PasswordRecoveryProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalPlainAuthenticationService extends LocalHashAuthenticationService implements PasswordRecoveryProvider {
    
    public LocalPlainAuthenticationService() {
        // set a "null" hash on the base class
        setPasswordHasher(new PasswordHasher() {
            public String hashPassword(String password) {
                return password;
            }
        });
    }
    
    public String getPassword(LiteYukonUser user) {
        String password = getYukonUserPasswordDao().recoverPassword(user);
        return password;
    }
}
