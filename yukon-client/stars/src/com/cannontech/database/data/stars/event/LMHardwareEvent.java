package com.cannontech.database.data.stars.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareEvent extends LMCustomerEventBase {

    private com.cannontech.database.db.stars.event.LMHardwareEvent lmHardwareEvent = null;

    public LMHardwareEvent() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getLMHardwareEvent().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMHardwareEvent().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLMHardwareEvent().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getLMHardwareEvent().setEventID( getLMCustomerEventBase().getEventID() );
        getLMHardwareEvent().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getLMHardwareEvent().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getLMHardwareEvent().retrieve();
    }
    
    public static void deleteAllLMHardwareEvents(Integer invID) {
		try {
    		Integer[] eventIDs = com.cannontech.database.db.stars.event.LMHardwareEvent.getAllLMHardwareEventIDs( invID );
    		
    		LMHardwareEvent event = new LMHardwareEvent();
    		for (int i = 0; i < eventIDs.length; i++) {
    			event.setEventID( eventIDs[i] );
    			Transaction.createTransaction( Transaction.DELETE, event ).execute();
    		}
    	}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
    }
    
    public static LMHardwareEvent[] getAllLMHardwareEvents(Integer invID) {
		try {
	        Integer[] eventIDs = com.cannontech.database.db.stars.event.LMHardwareEvent.getAllLMHardwareEventIDs( invID );
	        
	        LMHardwareEvent[] events = new LMHardwareEvent[ eventIDs.length ];
	        for (int i = 0; i < events.length; i++) {
	        	events[i] = new LMHardwareEvent();
				events[i].setEventID( eventIDs[i] );
				events[i] = (LMHardwareEvent) Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
	        }
	        
	        return events;
    	}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
    	return null;
    }

    public static LMHardwareEvent getLastLMHardwareEvent(Integer invID) {
    	try {
	        com.cannontech.database.db.stars.event.LMHardwareEvent eventDB =
	        		com.cannontech.database.db.stars.event.LMHardwareEvent.getLastLMHardwareEvent( invID );
	        if (eventDB == null) return null;
	        
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			event.setEventID( eventDB.getEventID() );
			
			event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
					Transaction.createTransaction( Transaction.RETRIEVE, event ).execute();
	        
	        return event;
    	}
    	catch (TransactionException e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }
    
	/**
	 * Returns the lmHardwareEvent.
	 * @return com.cannontech.database.db.stars.event.LMHardwareEvent
	 */
	public com.cannontech.database.db.stars.event.LMHardwareEvent getLMHardwareEvent() {
		if (lmHardwareEvent == null)
			lmHardwareEvent = new com.cannontech.database.db.stars.event.LMHardwareEvent();
		return lmHardwareEvent;
	}

	/**
	 * Sets the lmHardwareEvent.
	 * @param lmHardwareEvent The lmHardwareEvent to set
	 */
	public void setLmHardwareEvent(
		com.cannontech.database.db.stars.event.LMHardwareEvent lmHardwareEvent) {
		this.lmHardwareEvent = lmHardwareEvent;
	}

}