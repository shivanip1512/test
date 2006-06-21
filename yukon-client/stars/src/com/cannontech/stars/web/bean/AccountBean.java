package com.cannontech.stars.web.bean;

import java.util.List;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.stars.event.EventAccount;


public class AccountBean 
{
    private String currentCommercialType;
    private YukonSelectionList customerTypes;
    private int currentAccount;
    private List<EventAccount> currentEvents;
            
    public YukonSelectionList getCustomerTypes()
    {
        if(customerTypes == null)
        {
            customerTypes = new YukonSelectionList();
            customerTypes = DaoFactory.getYukonListDao().getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CUSTOMER_TYPE);
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
