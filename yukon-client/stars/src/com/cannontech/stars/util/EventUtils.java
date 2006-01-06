package com.cannontech.stars.util;

import com.cannontech.database.db.stars.event.EventBase;
import com.cannontech.database.db.stars.event.EventAccount;
import com.cannontech.database.db.stars.event.EventInventory;
import com.cannontech.database.db.stars.event.EventWorkOrder;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import java.util.Date;
import java.util.GregorianCalendar;


public class EventUtils 
{
    public static final String EVENT_CATEGORY_ACCOUNT = "CustomerAccount";
    public static final String EVENT_CATEGORY_INVENTORY = "Inventory";
    public static final String EVENT_CATEGORY_WORKORDER = "WorkOrder";
        
    public static void logSTARSEvent(int userID, String sysCategory, int actionID, int objectID)
    {
        EventBase event = new EventBase();
             
        event.setUserID(new Integer(userID));
        if(sysCategory.compareTo(EVENT_CATEGORY_ACCOUNT) == 0)
        {
            event.setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_ACCOUNT));
            EventAccount account = new EventAccount();
            account.setAccountID(new Integer(objectID));
            event.setEventAccount(account);
        }
        if(sysCategory.compareTo(EVENT_CATEGORY_INVENTORY) == 0)
        {
            event.setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_INVENTORY));
            EventInventory inven = new EventInventory();
            inven.setInventoryID(new Integer(objectID));
            event.setEventInventory(inven);
        }
        if(sysCategory.compareTo(EVENT_CATEGORY_WORKORDER) == 0)
        {
            event.setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_WORKORDER));
            EventWorkOrder order = new EventWorkOrder();
            order.setWorkOrderID(new Integer(objectID));
            event.setEventWorkOrder(order);
        }

        event.setActionID(new Integer(actionID));
        
        GregorianCalendar tempImp = new GregorianCalendar();
        long nowInMilliSeconds = tempImp.getTime().getTime();
        event.setEventTimestamp(new Date(nowInMilliSeconds));
        try
        {
            Transaction.createTransaction(Transaction.INSERT, event).execute(); 
        }
        catch( TransactionException e )
        {
            CTILogger.error( e.getMessage(), e );
        }
    }

}
