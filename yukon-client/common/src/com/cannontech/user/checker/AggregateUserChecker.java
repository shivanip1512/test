package com.cannontech.user.checker;

import java.util.Collection;
import java.util.Collections;

import com.cannontech.database.data.lite.LiteYukonUser;

public class AggregateUserChecker extends UserCheckerBase {
    private Collection<UserChecker> checkerList = Collections.emptyList();
    
    
    public AggregateUserChecker(Collection<UserChecker> checkerList) {
        this.checkerList = checkerList;
    }


    public boolean check(LiteYukonUser user) {
        for (UserChecker checker : checkerList) {
            if (!checker.check(user)) {
                return false;
            }
        }
        return true;
    }


    public void setCheckerList(
            Collection<UserChecker> checkerList) {
        this.checkerList = checkerList;
    }

}
