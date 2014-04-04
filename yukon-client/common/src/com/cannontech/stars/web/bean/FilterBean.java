package com.cannontech.stars.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
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
    private ArrayList<?> assignedFilters = new ArrayList();
    private YukonListEntry defaultFilterSelection;
    private String filterListName = YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
    
    private final String noneString = CtiUtilities.STRING_NONE;

	private class CodeComparator implements Comparator<ServiceCompanyDesignationCode>, Serializable
	{
		@Override
        public int compare(ServiceCompanyDesignationCode code1, ServiceCompanyDesignationCode code2){
		    String thisVal = code1.getDesignationCodeValue();
		    String anotherVal = code2.getDesignationCodeValue();
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	private final CodeComparator codeComparator = new CodeComparator();
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
        return YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, currentUser) && (energyCompany.hasChildEnergyCompanies());
    }
    
    public YukonSelectionList getAvailableFilters() {
        if (availableFilters == null){
            availableFilters = getSelectionList(filterListName);
        }
        return availableFilters;
    }
    
    public boolean isRangeValid() {
        EnergyCompanySettingDao energyCompanySettingDao = YukonSpringHook.getBean(EnergyCompanySettingDao.class);
        SerialNumberValidation value = energyCompanySettingDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, energyCompany.getEnergyCompanyId());
        return (value == SerialNumberValidation.NUMERIC);
    }
    
    public List<LiteStarsEnergyCompany> getAvailableMembers()
    {
        if(availableMembers == null) {
            availableMembers = ECUtils.getAllDescendants(energyCompany);
        }
        return availableMembers;
    }
    
    public YukonSelectionList getAvailableDeviceTypes()
    {
        if(availableDeviceTypes == null) {
            availableDeviceTypes = getSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
        }
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
        	for(int i = 0; i < getAvailableServiceCompanies().size(); i++) {
                availableDesignationCodes.addAll(getAvailableServiceCompanies().get(i).getDesignationCodes());
            }
        	
        	
        	Collections.sort(availableDesignationCodes, codeComparator);
        }
        return availableDesignationCodes;
    }

    public List<Warehouse> getAvailableWarehouses()
    {
        if(availableWarehouses == null) {
            availableWarehouses = energyCompany.getAllWarehousesDownward();
        }
        return availableWarehouses;
    }
    
    public YukonSelectionList getAvailableDeviceStates()
    {
        if(availableDeviceStates == null) {
            availableDeviceStates = getSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS);
        }
        return availableDeviceStates;
    }
    
    public YukonSelectionList getAvailableServiceTypes()
    {
        if(availableServiceTypes == null) {
            availableServiceTypes = getSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE);
        }
        return availableServiceTypes;
    }

    public YukonSelectionList getAvailableServiceStatuses()
    {
        if(availableServiceStatuses == null) {
            availableServiceStatuses = getSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
        }
        return availableServiceStatuses;
    }

    public ArrayList getAssignedFilters()
    {
        return assignedFilters;
    }
    
    public void setAssignedFilters(ArrayList newFilters)
    {
        if(newFilters != null) {
            assignedFilters = newFilters;
        }
    }

    public YukonListEntry getDefaultFilterSelection() {
    	if( defaultFilterSelection == null)
    	{
	    	if( getFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY) {
                defaultFilterSelection = getAvailableServiceStatuses().getYukonListEntries().get(0);
            } else if( getFilterListName() == YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY) {
                defaultFilterSelection = getAvailableDeviceTypes().getYukonListEntries().get(0);
            } else {
                defaultFilterSelection = getAvailableDeviceTypes().getYukonListEntries().get(0);
            }
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
            YukonSelectionList ciCustTypes = getSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE);
               
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
    
    private YukonSelectionList getSelectionList(String listName) {
        SelectionListService listService = YukonSpringHook.getBean(SelectionListService.class);
        return listService.getSelectionList(energyCompany, listName);
    }
    
}
