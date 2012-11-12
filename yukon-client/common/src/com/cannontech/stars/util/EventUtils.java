package com.cannontech.stars.util;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.event.EventAccount;
import com.cannontech.stars.database.data.event.EventBase;
import com.cannontech.stars.database.data.event.EventInventory;
import com.cannontech.stars.database.data.event.EventWorkOrder;


public class EventUtils {
    public static final String EVENT_CATEGORY_ACCOUNT = "CustomerAccount";
    public static final String EVENT_CATEGORY_INVENTORY = "Inventory";
    public static final String EVENT_CATEGORY_WORKORDER = "WorkOrder";
        
    
    public static EventBase logSTARSEvent(int userID, String sysCategory, int actionID, int objectID) {
        return logSTARSDatedEvent(userID, sysCategory, actionID, objectID, new Date());
    }
    
    public static EventBase logSTARSDatedEvent(int userID, String sysCategory, int actionID, int objectID, Date eventDate) {
        EventBase eventBase;
        if(sysCategory.compareTo(EVENT_CATEGORY_ACCOUNT) == 0) {
        	eventBase = new EventAccount();
            eventBase.getEventBase().setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_ACCOUNT));
            ((EventAccount)eventBase).getEventAccount().setAccountID(new Integer(objectID));
        }
        else if(sysCategory.compareTo(EVENT_CATEGORY_INVENTORY) == 0) {
        	eventBase = new EventInventory();
            eventBase.getEventBase().setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_INVENTORY));
            ((EventInventory)eventBase).getEventInventory().setInventoryID(new Integer(objectID));
        }
        else if(sysCategory.compareTo(EVENT_CATEGORY_WORKORDER) == 0) {
        	eventBase = buildEventWorkOrder(userID, actionID, objectID);
        }
        else	//TODO this is a bad catch all
        	eventBase = new EventBase();
        
        eventBase.getEventBase().setUserID(new Integer(userID));
        eventBase.getEventBase().setActionID(new Integer(actionID));
        if( eventDate == null)
        	eventDate = new Date();
        eventBase.getEventBase().setEventTimestamp(eventDate);
        
        try {
            Transaction.createTransaction(Transaction.INSERT, eventBase).execute(); 
        }
        catch( TransactionException e ) {
            CTILogger.error( e.getMessage(), e );
        }
        return eventBase;
    }
    
    public static EventWorkOrder buildEventWorkOrder(int userID, int actionID, int objectID) {
        EventWorkOrder  eventWorkOrder = new EventWorkOrder();
        eventWorkOrder.getEventBase().setSystemCategoryID(new Integer(YukonListEntryTypes.EVENT_SYS_CAT_WORKORDER));
        eventWorkOrder.getEventWorkOrder().setWorkOrderID(new Integer(objectID));
        eventWorkOrder.getEventBase().setUserID(new Integer(userID));
        eventWorkOrder.getEventBase().setActionID(new Integer(actionID));
        eventWorkOrder.getEventBase().setEventTimestamp(new Date());
        
        return eventWorkOrder;
    }
}
