package com.cannontech.stars.database.data.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatManualEvent extends LMCustomerEventBase {
	
	private com.cannontech.stars.database.db.event.LMThermostatManualEvent lmThermostatManualEvent = null;

    public LMThermostatManualEvent() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getLmThermostatManualEvent().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLmThermostatManualEvent().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLmThermostatManualEvent().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getLmThermostatManualEvent().setEventID( getLMCustomerEventBase().getEventID() );
        getLmThermostatManualEvent().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getLmThermostatManualEvent().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getLmThermostatManualEvent().retrieve();
    }
    
    public static void deleteAllLMThermostatManualEvents(int invID) {
    	try {
    		Integer[] eventIDs = com.cannontech.stars.database.db.event.LMThermostatManualEvent.getAllLMThermostatManualEventIDs( invID );
    		
    		LMThermostatManualEvent event = new LMThermostatManualEvent();
    		for (int i = 0; i < eventIDs.length; i++) {
    			event.setEventID( eventIDs[i] );
    			Transaction.createTransaction( Transaction.DELETE, event ).execute();
    		}
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    }
    
    public static LMThermostatManualEvent[] getAllLMThermostatManualEvents(int invID) {
		try {
	        Integer[] eventIDs = com.cannontech.stars.database.db.event.LMThermostatManualEvent.getAllLMThermostatManualEventIDs( invID );
	        
	        LMThermostatManualEvent[] events = new LMThermostatManualEvent[ eventIDs.length ];
	        for (int i = 0; i < events.length; i++) {
	        	events[i] = new LMThermostatManualEvent();
				events[i].setEventID( eventIDs[i] );
				events[i] = (LMThermostatManualEvent) Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
	        }
	        
	        return events;
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
    	return null;
    }

    public static LMThermostatManualEvent getLastLMThermostatManualEvent(Integer invID) {
    	try {
	        com.cannontech.stars.database.db.event.LMThermostatManualEvent eventDB =
	        		com.cannontech.stars.database.db.event.LMThermostatManualEvent.getLastLMThermostatManualEvent( invID );
	        if (eventDB == null) return null;
	        
        	com.cannontech.stars.database.data.event.LMThermostatManualEvent event =
        			new com.cannontech.stars.database.data.event.LMThermostatManualEvent();
			event.setEventID( eventDB.getEventID() );
        	
        	event = (com.cannontech.stars.database.data.event.LMThermostatManualEvent)
        			Transaction.createTransaction( Transaction.RETRIEVE, event ).execute();
	        
	        return event;
    	}
    	catch (TransactionException e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }
    
	/**
	 * Returns the lmThermostatManualEvent.
	 * @return com.cannontech.stars.database.db.event.LMThermostatManualEvent
	 */
	public com.cannontech.stars.database.db.event.LMThermostatManualEvent getLmThermostatManualEvent() {
		if (lmThermostatManualEvent == null)
			lmThermostatManualEvent = new com.cannontech.stars.database.db.event.LMThermostatManualEvent();
		return lmThermostatManualEvent;
	}

	/**
	 * Sets the lmThermostatManualEvent.
	 * @param lmThermostatManualEvent The lmThermostatManualEvent to set
	 */
	public void setLmThermostatManualEvent(
		com.cannontech.stars.database.db.event.LMThermostatManualEvent lmThermostatManualEvent) {
		this.lmThermostatManualEvent = lmThermostatManualEvent;
	}

}
