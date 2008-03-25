package com.cannontech.web.menu.option.producer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;

/**
 * Abstract base class for menu option producers
 */
public abstract class MenuOptionProducerBase implements MenuOptionProducer {

    private String id;
    private UserChecker checker;

    public String getId() {
        return id;
    }

    @Override
    public boolean isValidForUser(YukonUserContext userContext) {
        return checker.check(userContext.getYukonUser());
    }

    @Override
    public void setId(String topOptionId) {
        this.id = topOptionId;
    }

    @Override
    public void setPropertyChecker(UserChecker checker) {
        this.checker = checker;
    }

    @Override
    public boolean hasChildren(YukonUserContext userContext) {
        return false;
    }
    
    @Override
    public Iterator<MenuOptionProducer> getChildren(YukonUserContext userContext) {
        List<MenuOptionProducer> producerList = Collections.emptyList();
        return producerList.iterator();
    }

}
