package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.stars.purchasing.DeliverySchedule;
import com.cannontech.database.db.stars.purchasing.PurchasePlan;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;


public class PurchaseBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private List<LiteStarsEnergyCompany> availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<PurchasePlan> availablePlans;
    private int numberOfPlans;
    private PurchasePlan currentPlan;
    private DeliverySchedule currentSchedule;
    
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

    public List<PurchasePlan> getAvailablePlans() 
    {
        if(getManageMembers())
            availablePlans = PurchasePlan.getAllPurchasePlansForAllMembers(energyCompany.getEnergyCompanyID(), getAvailableMembers());
        else
            availablePlans = PurchasePlan.getAllPurchasePlans(energyCompany.getEnergyCompanyID());
        
        return availablePlans;
    }

    public void setAvailablePlans(List<PurchasePlan> availablePlans) 
    {
        this.availablePlans = availablePlans;
    }

    public PurchasePlan getCurrentPlan() 
    {
        /**
         * Grab the most recently created plan in the db.
         */
        if(currentPlan == null)
        {
            if(getAvailablePlans().size() > 0)
                currentPlan = availablePlans.get(0);
            else
                currentPlan = new PurchasePlan();
        }
        return currentPlan;
    }

    public void setCurrentPlan(PurchasePlan currentPlan) 
    {
        this.currentPlan = currentPlan;
    }

    public DeliverySchedule getCurrentSchedule() {
        return currentSchedule;
    }

    public void setCurrentSchedule(DeliverySchedule currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    public int getNumberOfPlans() {
        return availablePlans.size();
    }

    
}
