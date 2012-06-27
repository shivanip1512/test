package com.cannontech.user.checker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AggregateAndUserChecker extends UserCheckerBase {
    private final static Logger log = YukonLogManager.getLogger(AggregateAndUserChecker.class);

    private Collection<UserChecker> checkerList = Collections.emptyList();

    public AggregateAndUserChecker(Collection<UserChecker> checkerList) {
        this.checkerList = checkerList;
    }

    public AggregateAndUserChecker(UserChecker... checkers) {
        this.checkerList = Arrays.asList(checkers);
    }

    public boolean check(LiteYukonUser user) {
        LogHelper.debug(log, "check %s", toString());
        for (UserChecker checker : checkerList) {
            if (!checker.check(user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "aggregate and(" + checkerList + ") checker";
    }
}
