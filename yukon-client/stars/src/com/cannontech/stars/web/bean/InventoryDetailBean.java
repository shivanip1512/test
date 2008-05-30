package com.cannontech.stars.web.bean;

import java.util.Date;
import java.util.List;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.event.EventInventory;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;



public class InventoryDetailBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private YukonSelectionList availableDeviceTypes;
    private List<LiteServiceCompany> availableServiceCompanies;
    private List<Warehouse> availableWarehouses;
    private YukonSelectionList availableDeviceStates;
    
    private LiteInventoryBase currentInventory;
    private int currentInventoryID;
    private Warehouse currentWarehouse;
    private List<EventInventory> currentEvents;
    
    public LiteStarsEnergyCompany getEnergyCompany()
    {
        return energyCompany;
    }
    
    public void setEnergyCompany(LiteStarsEnergyCompany company)
    {
        energyCompany = company;
    }
    
    public LiteYukonUser getCurrentUser()
    {
        return currentUser;
    }
    
    public void setCurrentUser(LiteYukonUser user)
    {
        currentUser = user;
    }
    
    public boolean getManageMembers()
    {
        return DaoFactory.getAuthDao().checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.getChildren().size() > 0);
    }
    
    public YukonSelectionList getAvailableDeviceTypes()
    {
        if(availableDeviceTypes == null)
            availableDeviceTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, true, true);
        return availableDeviceTypes;
    }
    
    public List<LiteServiceCompany> getAvailableServiceCompanies()
    {
        if(availableServiceCompanies == null)
            availableServiceCompanies = energyCompany.getAllServiceCompaniesDownward();
        return availableServiceCompanies;
    }

    public List<Warehouse> getAvailableWarehouses()
    {
        if(availableWarehouses == null)
            availableWarehouses = energyCompany.getAllWarehousesDownward();
        return availableWarehouses;
    }
    
    public YukonSelectionList getAvailableDeviceStates()
    {
        if(availableDeviceStates == null)
            availableDeviceStates = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true, true);
        return availableDeviceStates;
    }

    public LiteInventoryBase getCurrentInventory() {
        return currentInventory;
    }

    public void setCurrentInventory(LiteInventoryBase currentInventory) {
        this.currentInventory = currentInventory;
    }

    public Warehouse getCurrentWarehouse() {
        if(currentWarehouse == null)
            currentWarehouse = Warehouse.getWarehouseFromInventoryID(getCurrentInventory().getInventoryID());
        return currentWarehouse;
    }

    public void setCurrentWarehouse(Warehouse currentWarehouse) {
        this.currentWarehouse = currentWarehouse;
    }
    
    public List<EventInventory> getCurrentEvents() {
        if(currentEvents == null)
        {
            currentEvents = EventInventory.retrieveEventInventories(getCurrentInventory().getInventoryID());
        }
        
        return currentEvents;
    }

    public void setCurrentEvents(List<EventInventory> currentEvents) {
        this.currentEvents = currentEvents;
    }

    public Date getDateOfInventoryEvent( int deviceStateDefID )
    {
        /*
         * Will take the most recent occurrence of this type
         * since events are ordered by descending timestamp
         */
        for(int i = 0; i < getCurrentEvents().size(); i++)
        {
            if(getCurrentEvents().get(i).getActionYukDefID() == deviceStateDefID)
                return getCurrentEvents().get(i).getEventBase().getEventTimestamp();
        }
        
        return null;
    }

    public int getCurrentInventoryID() {
        return currentInventoryID;
    }

    public void setCurrentInventoryID(int currentInventoryID) {
        this.currentInventoryID = currentInventoryID;
        currentInventory = energyCompany.getInventory( currentInventoryID, true );
    }
}
