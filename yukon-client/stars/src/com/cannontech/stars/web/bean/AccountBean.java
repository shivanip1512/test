package com.cannontech.stars.web.bean;

import com.cannontech.common.constants.*;
import com.cannontech.database.cache.functions.YukonListFuncs;


public class AccountBean 
{
    private String currentCommercialType;
    private YukonSelectionList customerTypes;
            
    public YukonSelectionList getCustomerTypes()
    {
        if(customerTypes == null)
        {
            customerTypes = new YukonSelectionList();
            customerTypes = YukonListFuncs.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CUSTOMER_TYPE);
        }
        
        return customerTypes;
    }

    public String getCurrentCommercialType() {
        return currentCommercialType;
    }

    public void setCurrentCommercialType(String currentCommercialType) {
        this.currentCommercialType = currentCommercialType;
    }
   
}
