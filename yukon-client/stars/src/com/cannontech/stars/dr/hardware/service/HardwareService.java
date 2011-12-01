package com.cannontech.stars.dr.hardware.service;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;

public interface HardwareService {
	
    /**
     * If delete is true: deletes the hardware, otherwise just removes it from the 
     * account and places it back in general inventory.
     */
    public void deleteHardware(YukonUserContext userContext, boolean delete, int inventoryId) 
    throws NotFoundException, CommandCompletionException, SQLException, PersistenceException, WebClientException;

    /**
     * Changes the device type of the inventory
     * @param context
     * @param inventory
     * @param type
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public void changeType(YukonUserContext context, InventoryIdentifier inv, HardwareType type) throws ObjectInOtherEnergyCompanyException;
    
    /**
     * Change the device status of the inventory, The session is needed since
     * this change will spawn an event and the event needs to be attached to the
     * parent energy company user if they are managing a member.
     * @param context
     * @param inv
     * @param statusEntryId The new device status yukon list entry id
     * @param session 
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public void changeDeviceStatus(YukonUserContext context, InventoryIdentifier inv, int statusEntryId, HttpSession session) throws ObjectInOtherEnergyCompanyException;

    /**
     * Change the service company of the inventory
     * @param context
     * @param inv
     * @param serviceCompanyId
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public void changeServiceCompany(YukonUserContext context, InventoryIdentifier inv, int serviceCompanyId) throws ObjectInOtherEnergyCompanyException;

    /**
     * Change the warehouse of the inventory
     * @param context
     * @param inv
     * @param warehouseId
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public void changeWarehouse(YukonUserContext context, InventoryIdentifier inv, int warehouseId) throws ObjectInOtherEnergyCompanyException;
    
}