package com.cannontech.web.menu.option.producer;

import com.cannontech.user.checker.AggregateUserChecker;
import com.cannontech.user.checker.UserChecker;

/**
 * Abstract base class for menu option producers
 */
public abstract class MenuOptionProducerBase implements MenuOptionProducer {

    private UserChecker userChecker = null;
    
    public void addUserChecker(UserChecker newUserChecker) {
        if (userChecker == null) {
            userChecker = newUserChecker;
        } else {
            userChecker = new AggregateUserChecker(userChecker, newUserChecker);
        }
        
    }
    
    

}
