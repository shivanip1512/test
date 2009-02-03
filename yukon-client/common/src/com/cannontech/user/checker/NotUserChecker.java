package com.cannontech.user.checker;

import com.cannontech.database.data.lite.LiteYukonUser;

public class NotUserChecker extends UserCheckerBase {
    private final UserChecker delegate;

    public NotUserChecker(UserChecker delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public boolean check(LiteYukonUser user) {
        return !delegate.check(user);
    }
}
