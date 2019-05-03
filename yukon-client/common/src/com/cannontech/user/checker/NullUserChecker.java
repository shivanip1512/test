package com.cannontech.user.checker;

import java.io.Serializable;

import com.cannontech.database.data.lite.LiteYukonUser;

public class NullUserChecker extends UserCheckerBase implements Serializable {

    private static final NullUserChecker instance = new NullUserChecker();
    
    private NullUserChecker() {
    }

    public boolean check(LiteYukonUser user) {
        return true;
    }
    
    @Override
    public String toString() {
        return "any user checker";
    }
    
    public static NullUserChecker getInstance() {
        return instance;
    }

}
