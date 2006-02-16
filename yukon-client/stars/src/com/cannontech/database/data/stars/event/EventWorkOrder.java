package com.cannontech.database.data.stars.event;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EventWorkOrder extends EventBase {

    private com.cannontech.database.db.stars.event.EventWorkOrder eventWorkOrder = null;

    public EventWorkOrder() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getEventWorkOrder().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getEventWorkOrder().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getEventWorkOrder().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getEventWorkOrder().setEventID( getEventBase().getEventID() );
        getEventWorkOrder().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getEventWorkOrder().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getEventWorkOrder().retrieve();
    }
    
    /*public static void deleteEventWorkOrder(int orderID) {
		try {
	    	Integer[] eventIDs = com.cannontech.database.db.stars.event.EventWorkOrder.getEventIDs( orderID );
	    	
	    	EventWorkOrder event = new EventWorkOrder();
	    	for (int i = 0; i < eventIDs.length; i++) {
	    		event.setEventID( eventIDs[i] );
	    		Transaction.createTransaction( Transaction.DELETE, event ).execute();
	    	}
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    }*/
    
	public static ArrayList<EventWorkOrder> retrieveEventWorkOrders(int orderID)
	{
		ArrayList<EventWorkOrder> eventWorkOrders = new ArrayList<EventWorkOrder>();
		String sql = "SELECT EB.EVENTID, USERID, SYSTEMCATEGORYID, ACTIONID, EVENTTIMESTAMP " +
					" FROM " + com.cannontech.database.db.stars.event.EventBase.TABLE_NAME + " EB, " +
					com.cannontech.database.db.stars.event.EventWorkOrder.TABLE_NAME + " EWO " +
					" WHERE EB.EVENTID = EWO.EVENTID " +
					" AND ORDERID = " + orderID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
        	for (int i = 0; i < stmt.getRowCount(); i++)
        	{
        		EventWorkOrder eventWorkOrder = new EventWorkOrder();
        		eventWorkOrder.setEventID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() ));
        		eventWorkOrder.getEventBase().setUserID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[1]).intValue() ));
        		eventWorkOrder.getEventBase().setSystemCategoryID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[2]).intValue() ));
        		eventWorkOrder.getEventBase().setActionID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[3]).intValue() ));
        		eventWorkOrder.getEventBase().setEventTimestamp((java.util.Date) stmt.getRow(i)[4] );
        		eventWorkOrder.getEventWorkOrder().setWorkOrderID(new Integer( orderID));
        	}
		}
		catch( Exception e ) {
		    CTILogger.error( e.getMessage(), e );
		}
		
		return eventWorkOrders;
	}
    
	/**
	 * Returns the eventWorkOrder.
	 * @return com.cannontech.database.db.stars.event.EventWorkOrder
	 */
	public com.cannontech.database.db.stars.event.EventWorkOrder getEventWorkOrder() {
		if (eventWorkOrder == null)
			eventWorkOrder = new com.cannontech.database.db.stars.event.EventWorkOrder();
		return eventWorkOrder;
	}

	/**
	 * Sets the eventWorkOrder.
	 * @param eventWorkOrder The eventWorkOrder to set
	 */
	public void setEventWorkOrder(
		com.cannontech.database.db.stars.event.EventWorkOrder eventWorkOrder) {
		this.eventWorkOrder = eventWorkOrder;
	}

}