package com.cannontech.stars.dr.hardware.service;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;

public interface HardwareService {
	
    /**
     * If delete is true: deletes the hardware, otherwise just removes it from the 
     * account and places it back in general inventory.
     */
    public void deleteHardware(LiteYukonUser user, boolean delete, int inventoryId) 
    throws NotFoundException, CommandCompletionException, SQLException, PersistenceException;

    /**
     * Changes the device type of the inventory
     * @param context
     * @param inventory
     * @param typeEntry
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public void changeType(YukonUserContext context, InventoryIdentifier inv, YukonListEntry typeEntry) throws ObjectInOtherEnergyCompanyException;
    
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

    /**
     * Create a single hardware based off a 'Add By SN Range' task iteration
     * @param abr the add by range task options
     * @param sn the serial number
     * @param user the energy company operator executing this task
     * @return inventory identifier of successfully created hardware
     * @throws ObjectInOtherEnergyCompanyException 
     */
    public InventoryIdentifier createForAddByRangeTask(AddByRange abr, long sn, LiteYukonUser user) throws ObjectInOtherEnergyCompanyException;
    
}