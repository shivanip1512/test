package com.cannontech.user.checker;

import com.cannontech.database.data.lite.LiteYukonUser;

public class NullUserChecker extends UserCheckerBase {

    public boolean check(LiteYukonUser user) {
        return true;
    }
    
    @Override
    public String toString() {
        return "any user checker";
    }

}
