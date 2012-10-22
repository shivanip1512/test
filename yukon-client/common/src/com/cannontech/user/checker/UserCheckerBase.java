package com.cannontech.user.checker;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class UserCheckerBase implements UserChecker {

    @Override
    public boolean check(LiteYukonUser user) {
        return false;
    }
    
    @Override
    public final void verify(LiteYukonUser user) throws NotAuthorizedException {
        if (check(user)) return;
        throw new NotAuthorizedException(toString());
    }

    public final static UserChecker FALSE = new UserCheckerBase() {
        @Override
        public boolean check(LiteYukonUser user) {
            return false;
        };
        
        @Override
        public String toString() {
            return "boolean checker (false)";
        }
    };
    
    public final static UserChecker TRUE = new UserCheckerBase() {
        @Override
        public boolean check(LiteYukonUser user) {
            return true;
        };
        
        @Override
        public String toString() {
            return "boolean checker (true)";
        }
    };
}
