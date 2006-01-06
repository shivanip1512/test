package com.cannontech.database.db.stars.event;

import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import java.util.ArrayList;
import com.cannontech.common.util.CtiUtilities;

public class EventBase extends DBPersistent {

    private Integer eventID;
    private Integer userID;
    private Integer systemCategoryID;
    private Integer actionID;
    private Date eventTimestamp;
    
    EventAccount eventAccount = null;
    EventInventory eventInventory = null;
    EventWorkOrder eventWorkOrder = null;

    public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

    public static final String SETTER_COLUMNS[] = { "UserID", "SystemCategoryID", "ActionID", "EventTimestamp" };

    public static final String TABLE_NAME = "EventBase";


public EventBase() {
    super();
}

public void add() throws java.sql.SQLException 
{
    if(eventID == null)
        eventID = getNextEventID();
    
    Object setValues[] = { getEventID(), getUserID(), getSystemCategoryID(), 
            getActionID(), getEventTimestamp() };

    add( TABLE_NAME, setValues );
    
    if(eventAccount != null)
    {
        eventAccount.setEventID(eventID);
        eventAccount.setDbConnection(this.getDbConnection());
        eventAccount.add();
    }
    if(eventInventory != null)
    {    
        eventInventory.setEventID(eventID);
        eventInventory.setDbConnection(this.getDbConnection());
        eventInventory.add();
    }
    if(eventWorkOrder != null)
    {
        eventWorkOrder.setEventID(eventID);
        eventWorkOrder.setDbConnection(this.getDbConnection());
        eventWorkOrder.add();
    }
}

public void delete() throws java.sql.SQLException {}


public static Integer getNextEventID() 
{
    SqlStatement stmt = new SqlStatement("SELECT MAX(EVENTID) + 1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            return (new Integer(stmt.getRow(0)[0].toString()));
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return new Integer(CtiUtilities.NONE_ZERO_ID);
}

public Integer getEventID() 
{
    return eventID;
}

public Integer getUserID() 
{
    return userID;
}

public Integer getSystemCategoryID() 
{
    return systemCategoryID;
}

public Integer getActionID() 
{
    return actionID;
}

public Date getEventTimestamp()
{
    return eventTimestamp;
}

public EventAccount getEventAccount()
{
    return eventAccount;
}

public EventInventory getEventInventory()
{
    return eventInventory;
}

public EventWorkOrder getEventWorkOrder()
{
    return eventWorkOrder;
}

public void retrieve() throws java.sql.SQLException {}

public void setEventID(Integer newID) 
{
    eventID = newID;
}

public void setUserID(Integer newUserID) 
{
    userID = newUserID;
}

public void setSystemCategoryID(Integer newSysCatID) 
{
    systemCategoryID = newSysCatID;
}

public void setActionID(Integer newID) 
{
    actionID = newID;
}

public void setEventTimestamp(Date now)
{
    eventTimestamp = now;
}

public void setEventAccount(EventAccount newEvent)
{
    eventAccount = newEvent;
}

public void setEventInventory(EventInventory newEvent)
{
    eventInventory = newEvent;
}

public void setEventWorkOrder(EventWorkOrder newEvent)
{
    eventWorkOrder = newEvent;
}

public void update() throws java.sql.SQLException {}

}
