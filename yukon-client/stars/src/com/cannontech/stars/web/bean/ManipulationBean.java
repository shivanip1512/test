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
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.database.cache.functions.AuthFuncs;


public class ManipulationBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private ArrayList availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<ServiceCompany> availableServiceCompanies;
    private YukonSelectionList availableDeviceStates;
    private List<Warehouse> availableWarehouses;
    
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
}
