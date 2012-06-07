package com.cannontech.stars.web.bean;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.util.ECUtils;


public class ManipulationBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private List<LiteStarsEnergyCompany> availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<LiteServiceCompany> availableServiceCompanies;
    private YukonSelectionList availableDeviceStates;
    private List<Warehouse> availableWarehouses;
    private YukonListEntry defaultActionSelection;
    
    private YukonSelectionList availableServiceTypes;
    private YukonSelectionList availableServiceStatuses;
    
    private String actionFilterListName = YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
    private int failures = 0;
    private List<String> failedManipulateResults = null;
    private int successes = 0;
    private List<String> actionsApplied = null;
    
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
    
    public YukonSelectionList getAvailableDeviceTypes()
    {
        if(availableDeviceTypes == null)
            availableDeviceTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, true, true);
        return availableDeviceTypes;
    }
    
    public List<LiteServiceCompany> getAvailableServiceCompanies()
    {
        if(availableServiceCompanies == null)
            availableServiceCompanies = energyCompany.getAllServiceCompaniesUpDown();
        return availableServiceCompanies;
    }
    
    public YukonSelectionList getAvailableDeviceStates()
    {
        if(availableDeviceStates == null)
            availableDeviceStates = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true, true);
        return availableDeviceStates;
    }
    
    public List<Warehouse> getAvailableWarehouses()
    {
        if(availableWarehouses == null)
            availableWarehouses = energyCompany.getAllWarehousesDownward();
        return availableWarehouses;
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
    
    public YukonListEntry getDefaultActionSelection() {
    	if( defaultActionSelection == null)
    	{
	    	if( getActionFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY)
	    		defaultActionSelection = getAvailableServiceStatuses().getYukonListEntries().get(0);
	    	else if( getActionFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY)
	    		defaultActionSelection = getAvailableDeviceTypes().getYukonListEntries().get(0);
	    	else
	    		defaultActionSelection = getAvailableDeviceTypes().getYukonListEntries().get(0);
    	}
        return defaultActionSelection;
    }

	public String getActionFilterListName() {
		return actionFilterListName;
	}

	public void setActionFilterListName(String actionFilterListName) {
		this.actionFilterListName = actionFilterListName;
	}
	
    public List<String> getActionsApplied() {
        return actionsApplied;
    }

    public void setActionsApplied(List<String> actionsApplied) {
        this.actionsApplied = actionsApplied;
    }

    public List<String> getFailedManipulateResults() {
        return failedManipulateResults;
    }

    public void setFailedManipulateResults(List<String> failedResults) {
        this.failedManipulateResults = failedResults;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int successes) {
        this.successes = successes;
    }
}
