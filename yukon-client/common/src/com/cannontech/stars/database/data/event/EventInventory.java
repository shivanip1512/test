package com.cannontech.stars.database.data.event;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.user.YukonUser;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EventInventory extends EventBase {

    private com.cannontech.stars.database.db.event.EventInventory eventInventory = null;

    public EventInventory() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getEventInventory().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getEventInventory().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getEventInventory().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getEventInventory().setEventID( getEventBase().getEventID() );
        getEventInventory().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getEventInventory().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getEventInventory().retrieve();
    }
    
 	/**
	 * Returns the eventInventory.
	 * @return com.cannontech.stars.database.db.event.EventInventory
	 */
	public com.cannontech.stars.database.db.event.EventInventory getEventInventory() {
		if (eventInventory == null)
			eventInventory = new com.cannontech.stars.database.db.event.EventInventory();
		return eventInventory;
	}

	/**
	 * Sets the eventInventory.
	 * @param eventInventory The eventInventory to set
	 */
	public void setEventInventory(
		com.cannontech.stars.database.db.event.EventInventory eventInventory) {
		this.eventInventory = eventInventory;
	}
    
    public static ArrayList<EventInventory> retrieveEventInventories(int inventoryID)
    {
        ArrayList<EventInventory> eventInventories = new ArrayList<EventInventory>();
        
        String sql = "SELECT EB.EVENTID, EB.USERID, SYSTEMCATEGORYID, ACTIONID, EVENTTIMESTAMP, YLE.ENTRYTEXT, YU.USERNAME, YLE.YUKONDEFINITIONID " +
                    " FROM " + com.cannontech.stars.database.db.event.EventBase.TABLE_NAME + " EB, " +
                    com.cannontech.stars.database.db.event.EventInventory.TABLE_NAME + " EI, " +
                    YukonListEntry.TABLE_NAME + " YLE, " +
                    YukonUser.TABLE_NAME + " YU " +
                    " WHERE EB.EVENTID = EI.EVENTID " +
                    " AND INVENTORYID = " + inventoryID +
                    " AND YLE.ENTRYID = EB.ACTIONID" +
                    " AND YU.USERID = EB.USERID" +
                    " ORDER BY EVENTTIMESTAMP DESC ";
        
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
            stmt.execute();
            for (int i = 0; i < stmt.getRowCount(); i++)
            {
                EventInventory eventInventory = new EventInventory();
                eventInventory.setEventID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() ));
                eventInventory.getEventBase().setUserID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[1]).intValue() ));
                eventInventory.getEventBase().setSystemCategoryID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[2]).intValue() ));
                eventInventory.getEventBase().setActionID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[3]).intValue() ));
                eventInventory.getEventBase().setEventTimestamp((java.util.Date) stmt.getRow(i)[4] );
                eventInventory.getEventInventory().setInventoryID(new Integer( inventoryID));
                eventInventory.setActionText(stmt.getRow(i)[5].toString());
                eventInventory.setUserName(stmt.getRow(i)[6].toString());
                eventInventory.setActionYukDefID( ((java.math.BigDecimal)stmt.getRow(i)[7]).intValue() );
                eventInventories.add(eventInventory);
            }
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return eventInventories;
    }

    
}