package com.cannontech.user.checker;

import com.cannontech.database.data.lite.LiteYukonUser;

public class SingleUserChecker extends UserCheckerBase{
    
    private LiteYukonUser singleUser;
    
    public SingleUserChecker(LiteYukonUser singleUser) {
        
        this.singleUser = singleUser; 
        
    }
    
    public boolean check(LiteYukonUser user) {
        if (user == null) {
            return false;
        }
        return user.equals(singleUser);
    };
    
    @Override
    public String toString() {
        return singleUser + " checker";
    }

}
