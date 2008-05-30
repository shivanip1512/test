package com.cannontech.stars.core.service;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to check user authorization
 */
public interface AccountCheckerService {

    public void checkInventory(LiteYukonUser user, Integer... inventoryIds) throws NotAuthorizedException;

    public void checkThermostatSchedule(LiteYukonUser user, Integer... scheduleIds) throws NotAuthorizedException;
    
    public void checkContact(LiteYukonUser user, Integer... contactIds) throws NotAuthorizedException;
    
    public void checkApplianceCategory(LiteYukonUser user, Integer... categoryIds) throws NotAuthorizedException;
    
    public void checkProgram(LiteYukonUser user, Integer... programIds) throws NotAuthorizedException;
    
    public void checkAppliance(LiteYukonUser user, Integer... applianceIds) throws NotAuthorizedException;
    
    public void checkCustomerAccount(LiteYukonUser user, Integer... customerAccountIds) throws NotAuthorizedException;

    /**
     * Method to check that a user is authorized to access a graph(s)
     * @param user - User in question
     * @param graphDefinitionIds - Ids of graphs user is trying to access
     * @throws NotAuthorizedException If user is not authorized
     */
    public void checkGraph(LiteYukonUser user, Integer... graphDefinitionIds) throws NotAuthorizedException;
    
    @Deprecated
    public void haltOnCheckInventory(LiteYukonUser user, Integer... inventoryIds) throws HaltOnErrorException;

    @Deprecated
    public void haltOnCheckThermostatSchedule(LiteYukonUser user, Integer... scheduleIds) throws HaltOnErrorException;

    @Deprecated
    public void haltOnCheckContact(LiteYukonUser user, Integer... contactIds) throws HaltOnErrorException;
    
    @Deprecated
    public void haltOnCheckApplianceCategory(LiteYukonUser user, Integer... categoryIds) throws HaltOnErrorException;

    @Deprecated
    public void haltOnCheckProgram(LiteYukonUser user, Integer... programIds) throws HaltOnErrorException;
    
    @Deprecated
    public void haltOnCheckAppliance(LiteYukonUser user, Integer... applianceIds) throws HaltOnErrorException;
    
    @Deprecated
    public void haltOnCheckCustomerAccount(LiteYukonUser user, Integer... customerAccountIds) throws HaltOnErrorException;
    
    public static final class HaltOnErrorException extends Error {
        
        public HaltOnErrorException(String message) {
            super(message);
        }
        
    }
    
}
