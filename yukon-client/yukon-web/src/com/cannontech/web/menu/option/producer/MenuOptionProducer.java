package com.cannontech.web.menu.option.producer;

import java.util.Iterator;
import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;

/**
 * Interface used to produce menu options
 */
public interface MenuOptionProducer {

    /**
     * Method to get a list of menu options from this producer
     * @param userContext - UserContext to get menu options for
     * @return List of user-specific menu options
     */
    public List<MenuOption> getMenuOptions(YukonUserContext userContext);

    /**
     * Method to get the child menu option producers for this producer
     * @param userContext - UserContext to get child menu option producers for
     * @return A list of user-specific child option producers
     */
    public Iterator<MenuOptionProducer> getChildren(YukonUserContext userContext);

    public void setPropertyChecker(UserChecker checker);

    public void setId(String topOptionId);

    /**
     * Method to see if this producer is valid for a given user
     * @param userContext - UserContext to check validity for
     * @return - True if valid
     */
    public boolean isValidForUser(YukonUserContext userContext);

    /**
     * Method to see if this producer has child producers for the given user
     * @param userContext - User context to determine if there are child
     *            producers for
     * @return True if there are child producers for the user
     */
    public boolean hasChildren(YukonUserContext userContext);
}
