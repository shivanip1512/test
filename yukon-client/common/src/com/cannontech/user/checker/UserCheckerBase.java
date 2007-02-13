package com.cannontech.user.checker;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class UserCheckerBase implements UserChecker {

    public boolean check(LiteYukonUser user) {
        return false;
    }
    
    public final void verify(LiteYukonUser user) throws NotAuthorizedException {
        if (check(user)) return;
        throw new NotAuthorizedException(toString());
    }

}
