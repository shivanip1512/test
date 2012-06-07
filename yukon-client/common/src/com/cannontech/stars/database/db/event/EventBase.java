package com.cannontech.stars.database.db.event;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class EventBase extends DBPersistent {

    private Integer eventID = null;
    private Integer userID = null;
    private Integer systemCategoryID = null;
    private Integer actionID = null;
    private Date eventTimestamp = null;
    
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
}

public void delete() throws java.sql.SQLException {
    Object[] constraintValues = { getEventID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}
public Integer getNextEventID() throws SQLException 
{
    return new Integer(YukonSpringHook.getNextValueHelper().getNextValue("EventBase"));
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

public void update() throws java.sql.SQLException {}

}
