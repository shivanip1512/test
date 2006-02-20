package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.constants.*;
import com.cannontech.database.DatabaseTypes;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;


public class NonYukonMeterBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private ArrayList availableMembers;
    private YukonSelectionList availableMeterTypes;
    private List<ServiceCompany> availableServiceCompanies;
    private MeterHardwareBase currentMeter;
    private YukonSelectionList voltages;
        
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
        return AuthFuncs.checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.getChildren().size() > 0);
    }
    
    public ArrayList getAvailableMembers()
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
            ArrayList tempList = new ArrayList();
            tempList.add(energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT));
            availableMeterTypes.setYukonListEntries(tempList);
        }
        return availableMeterTypes;
    }
    
    public List<ServiceCompany> getAvailableServiceCompanies()
    {
        if(availableServiceCompanies == null)
            availableServiceCompanies = energyCompany.getAllServiceCompaniesDownward();
        return availableServiceCompanies;
    }

    public MeterHardwareBase getCurrentMeter() 
    {
        return currentMeter;
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
}
