package com.cannontech.stars.web.bean;

import java.util.List;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.stars.database.data.event.EventAccount;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;


public class AccountBean {
    private LiteStarsEnergyCompany energyCompany = null;
    private String currentCommercialType;
    private YukonSelectionList customerTypes;
    private int currentAccount;
    private List<EventAccount> currentEvents;
            
    public LiteStarsEnergyCompany getEnergyCompany() {
        return energyCompany;
    }
    
    public void setEnergyCompany(LiteStarsEnergyCompany company) {
        energyCompany = company;
    }
    
    public YukonSelectionList getCustomerTypes() {
        if(customerTypes == null) {
            customerTypes = new YukonSelectionList();
            customerTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE, true, true);
        }
        
        return customerTypes;
    }

    public String getCurrentCommercialType() {
        return currentCommercialType;
    }

    public void setCurrentCommercialType(String currentCommercialType) {
        this.currentCommercialType = currentCommercialType;
    }

    public int getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(int currentAccount) {
        this.currentAccount = currentAccount;
    }

    public List<EventAccount> getCurrentEvents() {
        currentEvents = EventAccount.retrieveEventAccounts(getCurrentAccount());
        return currentEvents;
    }

    public void setCurrentEvents(List<EventAccount> currentEvents) {
        this.currentEvents = currentEvents;
    }
   
    
}
