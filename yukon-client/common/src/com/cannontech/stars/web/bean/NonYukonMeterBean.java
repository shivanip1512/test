package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.database.data.hardware.MeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Lists;


public class NonYukonMeterBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private List<LiteStarsEnergyCompany> availableMembers;
    private YukonSelectionList availableMeterTypes;
    private List<LiteServiceCompany> availableServiceCompanies;
    private MeterHardwareBase currentMeter;
    private int currentMeterID = -1;
    private YukonSelectionList voltages;
    private List<StarsInventory> currentAccountInventory;
    private Integer currentAccountID;
    
    /**
     * Dust this as soon as we remove xml objects such as StarsInventory
     */
    private List<Pair> availableSwitches;
    //mapping
    private List<Pair> assignedSwitches;
    
        
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
        return DaoFactory.getAuthDao().checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.hasChildEnergyCompanies());
    }
    
    public List<LiteStarsEnergyCompany> getAvailableMembers()
    {
        if(availableMembers == null)
            availableMembers = ECUtils.getAllDescendants(energyCompany);
        return availableMembers;
    }
    
    public YukonSelectionList getAvailableMeterTypes()
    {
        if(availableMeterTypes == null)
        {
            availableMeterTypes = new YukonSelectionList();
            List<YukonListEntry> tempList = Lists.newArrayList();
            tempList.add(energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER));
            availableMeterTypes.setYukonListEntries(tempList);
        }
        return availableMeterTypes;
    }
    
    public List<LiteServiceCompany> getAvailableServiceCompanies()
    {
        if(availableServiceCompanies == null)
            availableServiceCompanies = energyCompany.getAllServiceCompaniesDownward();
        return availableServiceCompanies;
    }

    public void setCurrentMeter(MeterHardwareBase currentMeter) 
    {
        this.currentMeter = currentMeter;
    }

    public YukonSelectionList getVoltages() 
    {
        if(voltages == null)
            voltages = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
        return voltages;
    }

    public void setVoltages(YukonSelectionList voltages) 
    {
        this.voltages = voltages;
    }

    public List<Pair> getAvailableSwitches() 
    {
        availableSwitches = new ArrayList<Pair>();
        
        for (int i = 0; i < currentAccountInventory.size(); i++) 
        {
            StarsInventory inv = currentAccountInventory.get(i);
            
            if(!MeterHardwareBase.isSwitchAssignedToAnyMeters(inv.getInventoryID()))
                availableSwitches.add(new Pair(inv.getInventoryID(), inv.getLMHardware().getManufacturerSerialNumber()));
        }
        
        return availableSwitches;
    }

    public void setAvailableSwitches(List<Pair> availableSwitches) {
        this.availableSwitches = availableSwitches;
    }

    public int getCurrentMeterID() {
        return currentMeterID;
    }

    public void setCurrentMeterID(int currentMeterID) {
        this.currentMeterID = currentMeterID;
    }
    
    public MeterHardwareBase getCurrentMeter()
    {
        if(currentMeterID == -1)
        {
            currentMeter = new MeterHardwareBase();
            return currentMeter;
        }
        else
        {        
            MeterHardwareBase retrieveMeter = new MeterHardwareBase();
            retrieveMeter.setInventoryID(currentMeterID);
            
            try
            {
                retrieveMeter = (MeterHardwareBase)Transaction.createTransaction(Transaction.RETRIEVE, retrieveMeter).execute();
            }
            catch(TransactionException e)
            {
                CTILogger.error( "Error retrieving meter with inventoryID of " + currentMeterID + ": " + e.getMessage(), e );
            }
            
            currentMeter = retrieveMeter;
        }
        
        return currentMeter;
    }

    public List<Pair> getAssignedSwitches() 
    {
        assignedSwitches = MeterHardwareBase.getAssignedSwitchesForDisplay(getCurrentMeterID());
        
        return assignedSwitches;
    }
    
    public void setAssignedSwitches(List<Pair> assignedSwitches) 
    {
        this.assignedSwitches = assignedSwitches;
    }

    /**
     * Dust this as soon as we remove these xml objects such as StarsInventory
     */
    public List<StarsInventory> getCurrentAccountInventory() {
        return currentAccountInventory;
    }

    public void setCurrentAccountInventory(List<StarsInventory> currentAccountInventory) {
        this.currentAccountInventory = currentAccountInventory;
    }

    public Integer getCurrentAccountID() {
        return currentAccountID;
    }

    public void setCurrentAccountID(Integer currentAccountID) {
        this.currentAccountID = currentAccountID;
    }
}
