package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.db.stars.hardware.Warehouse;


public class FilterBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private YukonSelectionList availableFilters;
    private ArrayList availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<ServiceCompany> availableServiceCompanies;
    private List<Warehouse> availableWarehouses;
    private YukonSelectionList availableDeviceStates;
   
    private YukonSelectionList availableServiceTypes;
    private YukonSelectionList availableServiceStatuses;
    
    boolean hasAssignedFilters = false;
    private ArrayList assignedFilters = new ArrayList();
    private YukonListEntry defaultFilterSelection;
    private String filterListName = YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
    
    /**
     * Need further filters:
     * --Appliances
     * --General location (need to figure out how to work new Warehouse idea into this)
     * --Addressing groups
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
            availableFilters = energyCompany.getYukonSelectionList(filterListName, true, true);
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
    
    public List<ServiceCompany> getAvailableServiceCompanies()
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
    
    public YukonSelectionList getAvailableServiceTypes()
    {
        if(availableServiceTypes == null)
            availableServiceTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE);
        return availableServiceTypes;
    }

    public YukonSelectionList getAvailableServiceStatuses()
    {
        if(availableServiceStatuses == null)
            availableServiceStatuses = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
        return availableServiceStatuses;
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
    	if( defaultFilterSelection == null)
    	{
	    	if( getFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY)
	    		defaultFilterSelection = (YukonListEntry)getAvailableServiceStatuses().getYukonListEntries().get(0);
	    	else if( getFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY)
	    		defaultFilterSelection = (YukonListEntry)getAvailableDeviceTypes().getYukonListEntries().get(0);
	    	else
	    		defaultFilterSelection = (YukonListEntry)getAvailableDeviceTypes().getYukonListEntries().get(0);
    	}
        return defaultFilterSelection;
    }

	public String getFilterListName() {
		return filterListName;
	}

	public void setFilterListName(String filterListName) {
		this.filterListName = filterListName;
	}
    
    
}
