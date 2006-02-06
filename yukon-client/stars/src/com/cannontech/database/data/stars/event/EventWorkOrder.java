package com.cannontech.database.data.stars.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;

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
    
	/*public static EventWorkOrder[] getAllEventWorkOrders(int orderID) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.EventWorkOrder.getEventIDs( orderID );
			
			EventWorkOrder[] events = new EventWorkOrder[ eventIDs.length ];
			for (int i = 0; i < events.length; i++) {
				events[i] = new EventWorkOrder();
				events[i].setEventID( eventIDs[i] );
				events[i] = (EventWorkOrder) Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
			}
	        
			return events;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
		return null;
	}*/
    
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