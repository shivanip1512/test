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

public class LMProgramEvent extends LMCustomerEventBase {

    private com.cannontech.database.db.stars.event.LMProgramEvent lmProgramEvent = null;

    public LMProgramEvent() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getLMProgramEvent().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMProgramEvent().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLMProgramEvent().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getLMProgramEvent().setEventID( getLMCustomerEventBase().getEventID() );
        getLMProgramEvent().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getLMProgramEvent().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getLMProgramEvent().retrieve();
    }
    
    public static void deleteAllLMProgramEvents(int accountID) {
		try {
	    	Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs( accountID );
	    	
	    	LMProgramEvent event = new LMProgramEvent();
	    	for (int i = 0; i < eventIDs.length; i++) {
	    		event.setEventID( eventIDs[i] );
	    		Transaction.createTransaction( Transaction.DELETE, event ).execute();
	    	}
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    }
    
    public static void deleteLMProgramEvents(int accountID, int programID) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getLMProgramEventIDs( accountID, programID );
			
    		LMProgramEvent event = new LMProgramEvent();
	    	for (int i = 0; i < eventIDs.length; i++) {
	    		event.setEventID( eventIDs[i] );
	    		Transaction.createTransaction( Transaction.DELETE, event ).execute();
	    	}
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    }
    
	public static void deleteAllLMProgramEvents(int energyCompanyID, int programID) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs(
					energyCompanyID, programID );
			
			LMProgramEvent event = new LMProgramEvent();
			for (int i = 0; i < eventIDs.length; i++) {
				event.setEventID( eventIDs[i] );
				Transaction.createTransaction( Transaction.DELETE, event ).execute();
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
    
	public static LMProgramEvent[] getAllLMProgramEvents(int accountID) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs( accountID );
			
			LMProgramEvent[] events = new LMProgramEvent[ eventIDs.length ];
			for (int i = 0; i < events.length; i++) {
				events[i] = new LMProgramEvent();
				events[i].setEventID( eventIDs[i] );
				events[i] = (LMProgramEvent) Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
			}
	        
			return events;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
		return null;
	}
    
    public static LMProgramEvent[] getLMProgramEvents(int accountID, int programID) {
		try {
	        Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getLMProgramEventIDs( accountID, programID );
	        
	        LMProgramEvent[] events = new LMProgramEvent[ eventIDs.length ];
	        for (int i = 0; i < events.length; i++) {
		    	events[i] = new LMProgramEvent();
		    	events[i].setEventID( eventIDs[i] );
				events[i] = (LMProgramEvent) Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
	        }
	        
	        return events;
    	}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
    	return null;
    }

	/**
	 * Returns the lmProgramEvent.
	 * @return com.cannontech.database.db.stars.event.LMProgramEvent
	 */
	public com.cannontech.database.db.stars.event.LMProgramEvent getLMProgramEvent() {
		if (lmProgramEvent == null)
			lmProgramEvent = new com.cannontech.database.db.stars.event.LMProgramEvent();
		return lmProgramEvent;
	}

	/**
	 * Sets the lmProgramEvent.
	 * @param lmProgramEvent The lmProgramEvent to set
	 */
	public void setLMProgramEvent(
		com.cannontech.database.db.stars.event.LMProgramEvent lmProgramEvent) {
		this.lmProgramEvent = lmProgramEvent;
	}

}