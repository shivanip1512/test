package com.cannontech.stars.database.db.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class EventWorkOrder extends DBPersistent {

    private Integer eventID;
    private Integer workOrderID;

    public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

    public static final String SETTER_COLUMNS[] = { "WorkOrderID" };

    public static final String TABLE_NAME = "EventWorkOrder";


public EventWorkOrder() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getEventID(), getWorkOrderID() };

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException {
    Object[] constraintValues = { getEventID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public Integer getEventID() 
{
    return eventID;
}

public Integer getWorkOrderID() 
{
    return workOrderID;
}

public void retrieve() throws java.sql.SQLException {}

public void setEventID(Integer newID) 
{
    eventID = newID;
}

public void setWorkOrderID(Integer newWorkOrderID) 
{
    workOrderID = newWorkOrderID;
}

public void update() throws java.sql.SQLException {}

public static Integer[] getAllEventWorkOrderIDs(Integer orderID) {
    String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE OrderID =" + orderID + " ORDER BY EventID";
    SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    
    try {
    	stmt.execute();
    	
    	Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
    	for (int i = 0; i < stmt.getRowCount(); i++)
    		eventIDs[i] = new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() );
    	
		return eventIDs;
    }
    catch( Exception e ) {
        CTILogger.error( e.getMessage(), e );
    }
    
    return null;
}

}
