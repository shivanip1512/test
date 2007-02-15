package com.cannontech.user.checker;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * A UserChecker is an object that has a single check() method
 * that takes a LiteYukonUser as its argument.
 */
public interface UserChecker extends Checker<LiteYukonUser>{
    
    /**
     * @param user The current user owning the session
     * @return true if the user passes the test
     */
    public boolean check(LiteYukonUser user);
 
    /**
     * @param user The current user owning the session
     * @throws NotAuthorizedException if the user does not passes the test
     */
    public void verify(LiteYukonUser user) throws NotAuthorizedException;

}
