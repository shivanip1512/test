package com.cannontech.database.db.stars.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareEvent extends DBPersistent {

    private Integer eventID = null;
    private Integer inventoryID = new Integer( CtiUtilities.NONE_ZERO_ID );

    public static final String[] SETTER_COLUMNS = { "InventoryID" };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMHardwareEvent";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

    public LMHardwareEvent() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getEventID(), getInventoryID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = { getInventoryID() };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setInventoryID( (Integer) results[0] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public static Integer[] getAllLMHardwareEventIDs(Integer inventoryID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE InventoryID =" + inventoryID + " ORDER BY EventID";
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
    
    public static LMHardwareEvent getLastLMHardwareEvent(Integer inventoryID) {
        String sql = "SELECT EventID, InventoryID FROM " + TABLE_NAME + " WHERE EventID = (" +
        		"SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID + ")";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
            
            if (stmt.getRowCount() > 0) {
            	Object[] row = stmt.getRow(0);
            	
                LMHardwareEvent event = new LMHardwareEvent();
                event.setEventID( new Integer(((java.math.BigDecimal)row[0]).intValue()) );
                event.setInventoryID( new Integer(((java.math.BigDecimal)row[1]).intValue()) );
                return event;
            }
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer newEventID) {
        eventID = newEventID;
    }

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(Integer newInventoryID) {
        inventoryID = newInventoryID;
    }
}