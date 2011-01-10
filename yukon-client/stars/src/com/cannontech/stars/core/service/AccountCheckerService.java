package com.cannontech.stars.core.service;

import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to check user authorization.
 */
public interface AccountCheckerService {

    public void checkInventory(LiteYukonUser user, Integer... inventoryIds) throws NotAuthorizedException;
    
    public void checkInventory(LiteYukonUser user, List<Integer> inventoryIds) throws NotAuthorizedException;
    
    public void checkThermostatSchedule(LiteYukonUser user, Integer... scheduleIds) throws NotAuthorizedException;
    
    public void checkContact(LiteYukonUser user, Integer... contactIds) throws NotAuthorizedException;
    
    public void checkProgram(LiteYukonUser user, Integer... programIds) throws NotAuthorizedException;
    
    /**
     * Method to check that a user is authorized to access a graph(s)
     * @param user - User in question
     * @param graphDefinitionIds - Ids of graphs user is trying to access
     * @throws NotAuthorizedException If user is not authorized
     */
    public void checkGraph(LiteYukonUser user, Integer... graphDefinitionIds) throws NotAuthorizedException;
    
}
