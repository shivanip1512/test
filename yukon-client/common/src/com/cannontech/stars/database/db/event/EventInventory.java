package com.cannontech.stars.database.db.event;

import com.cannontech.database.db.DBPersistent;

public class EventInventory extends DBPersistent {

    private Integer eventID;
    private Integer inventoryID;

    public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

    public static final String SETTER_COLUMNS[] = { "InventoryID" };

    public static final String TABLE_NAME = "EventInventory";


public EventInventory() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getEventID(), getInventoryID() };

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

public Integer getInventoryID() 
{
    return inventoryID;
}

public void retrieve() throws java.sql.SQLException {}

public void setEventID(Integer newID) 
{
    eventID = newID;
}

public void setInventoryID(Integer newInventoryID) 
{
    inventoryID = newInventoryID;
}

public void update() throws java.sql.SQLException {}

}
