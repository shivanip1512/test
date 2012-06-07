package com.cannontech.stars.database.db.event;

import com.cannontech.database.db.DBPersistent;

public class EventAccount extends DBPersistent {

    private Integer eventID;
    private Integer accountID;

    public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

    public static final String SETTER_COLUMNS[] = { "AccountID" };

    public static final String TABLE_NAME = "EventAccount";


public EventAccount() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getEventID(), getAccountID() };

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object[] constraintValues = { getEventID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public Integer getEventID() 
{
    return eventID;
}

public Integer getAccountID() 
{
    return accountID;
}

public void retrieve() throws java.sql.SQLException {}

public void setEventID(Integer newID) 
{
    eventID = newID;
}

public void setAccountID(Integer newAccountID) 
{
    accountID = newAccountID;
}

public void update() throws java.sql.SQLException {}

}
