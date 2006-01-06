package com.cannontech.stars.web.bean;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.database.cache.functions.AuthFuncs;


public class FilterBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private YukonSelectionList availableFilters;
    private ArrayList availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private ArrayList availableServiceCompanies;
    private YukonSelectionList availableDeviceStates;
    boolean hasAssignedFilters = false;
    private ArrayList assignedFilters = new ArrayList();
    private YukonListEntry defaultFilterSelection;
    
    /**
     * Need further filters:
     * --Appliances
     * --General location
     * --Addressing groups
     * --Warehouse
     * --Zip code
     * --Serial range
     */
            
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
    
    public YukonSelectionList getAvailableFilters()
    {
        if(availableFilters == null)
            availableFilters = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY, true, true);
        return availableFilters;
    }
    
    public ArrayList getAvailableMembers()
    {
        if(availableMembers == null)
            availableMembers = ECUtils.getAllDescendants(energyCompany);
        return availableMembers;
    }
    
    public YukonSelectionList getAvailableDeviceTypes()
    {
        if(availableDeviceTypes == null)
            availableDeviceTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, true, true);
        return availableDeviceTypes;
    }
    
    public ArrayList getAvailableServiceCompanies()
    {
        if(availableServiceCompanies == null)
            availableServiceCompanies = energyCompany.getServiceCompanies();
        return availableServiceCompanies;
    }
    
    public YukonSelectionList getAvailableDeviceStates()
    {
        if(availableDeviceStates == null)
            availableDeviceStates = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true, true);
        return availableDeviceStates;
    }
    
    public ArrayList getAssignedFilters()
    {
        return assignedFilters;
    }
    
    public void setAssignedFilters(ArrayList newFilters)
    {
        if(newFilters != null)
            assignedFilters = newFilters;
    }

    public YukonListEntry getDefaultFilterSelection() {
        defaultFilterSelection = (YukonListEntry)getAvailableDeviceTypes().getYukonListEntries().get(0);
        return defaultFilterSelection;
    }
    
    
}
