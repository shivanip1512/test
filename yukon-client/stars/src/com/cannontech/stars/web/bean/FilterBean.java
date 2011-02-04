package com.cannontech.stars.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.impl.EnergyCompanyRolePropertyDaoImpl.SerialNumberValidation;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;


public class FilterBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private YukonSelectionList availableFilters;
    private List<LiteStarsEnergyCompany> availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<LiteServiceCompany> availableServiceCompanies;
    private List<ServiceCompanyDesignationCode> availableDesignationCodes;
    private List<Warehouse> availableWarehouses;
    private YukonSelectionList availableDeviceStates;
    private List<LiteApplianceCategory> availableApplianceCategories;
    private ArrayList<Pair<Integer,String>> availableCustomerTypes;	//Integer(entryID),String(text)
   
    private YukonSelectionList availableServiceTypes;
    private YukonSelectionList availableServiceStatuses;
    
    boolean hasAssignedFilters = false;
    private ArrayList assignedFilters = new ArrayList();
    private YukonListEntry defaultFilterSelection;
    private String filterListName = YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
    
    private String noneString = CtiUtilities.STRING_NONE;

	private class CodeComparator implements Comparator<ServiceCompanyDesignationCode>, Serializable
	{
		public int compare(ServiceCompanyDesignationCode code1, ServiceCompanyDesignationCode code2){
		    String thisVal = code1.getDesignationCodeValue();
		    String anotherVal = code2.getDesignationCodeValue();
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	private CodeComparator codeComparator = new CodeComparator();
    /**
     * Need further filters:
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
        return DaoFactory.getAuthDao().checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.hasChildEnergyCompanies());
    }
    
    public YukonSelectionList getAvailableFilters() {
        if (availableFilters == null){
            availableFilters = energyCompany.getYukonSelectionList(filterListName, true, true);
            SerialNumberValidation value = DaoFactory.getEnergyCompanyRolePropertyDao().getPropertyEnumValue(
                YukonRoleProperty.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, energyCompany);
            if (value == SerialNumberValidation.ALPHANUMERIC) {
                YukonListEntry min = energyCompany.getYukonListEntry(filterListName, YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN);
                YukonListEntry max = energyCompany.getYukonListEntry(filterListName, YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX);
                availableFilters.getYukonListEntries().remove(min);
                availableFilters.getYukonListEntries().remove(max);
            }
            
        }
        return availableFilters;
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
        if(availableServiceCompanies == null) {
            availableServiceCompanies = energyCompany.getAllServiceCompaniesUpDown();
        }
        return availableServiceCompanies;
    }

    public List<ServiceCompanyDesignationCode> getAvailableDesignationCodes()
    {
        if(availableDesignationCodes == null)
        {
        	availableDesignationCodes = new ArrayList<ServiceCompanyDesignationCode>();
        	for(int i = 0; i < getAvailableServiceCompanies().size(); i++)
        		availableDesignationCodes.addAll(getAvailableServiceCompanies().get(i).getDesignationCodes());
        	
        	
        	Collections.sort(availableDesignationCodes, codeComparator);
        }
        return availableDesignationCodes;
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

    public List<LiteApplianceCategory> getAvailableApplianceCategories() {
    	Iterable<LiteApplianceCategory> applianceCategories = energyCompany.getAllApplianceCategories();
    	availableApplianceCategories = new ArrayList<LiteApplianceCategory>();
    	
    	for(LiteApplianceCategory category : applianceCategories) {
    		availableApplianceCategories.add(category);
    	}
        return availableApplianceCategories;
    }

    public void setAvailableApplianceCategories(
            List<LiteApplianceCategory> availableApplianceCategories) {
        this.availableApplianceCategories = availableApplianceCategories;
    }

	public ArrayList<Pair<Integer,String>>getAvailableCustomerTypes() {
        if(availableCustomerTypes == null)
        {
            availableCustomerTypes = new ArrayList<Pair<Integer,String>>();
            YukonSelectionList ciCustTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE, true, true);
               
        	availableCustomerTypes.add(new Pair<Integer,String>(new Integer(-1), new String("Residential")));	//we'll use -1 as Residential
        	
            if(ciCustTypes != null)
            {
                for(int i = 0; i < ciCustTypes.getYukonListEntries().size(); i++)
            	{
            		YukonListEntry entry = ciCustTypes.getYukonListEntries().get(i);
            		availableCustomerTypes.add(new Pair<Integer,String>(new Integer(entry.getEntryID()), entry.getEntryText()));
            	}
            }
        }
		return availableCustomerTypes;
	}

    public String getNoneString() {
        return noneString;
    }
    
    
}
