package com.cannontech.database.data.stars.event;

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
    
    public static void deleteAllLMProgramEvents(Integer accountID, java.sql.Connection conn) {
    	try {
	    	Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs( accountID, conn );
	    	
	    	com.cannontech.database.data.stars.event.LMProgramEvent event =
	    			new com.cannontech.database.data.stars.event.LMProgramEvent();
	    	
	    	for (int i = 0; i < eventIDs.length; i++) {
	    		event.setEventID( eventIDs[i] );
	    		event.setDbConnection( conn );
	    		event.delete();
	    	}
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public static void deleteLMProgramEvents(Integer accountID, Integer programID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return;
            
    		Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getLMProgramEventIDs( accountID, programID );
    		
    		com.cannontech.database.data.stars.event.LMProgramEvent event =
    				new com.cannontech.database.data.stars.event.LMProgramEvent();
    		
	    	for (int i = 0; i < eventIDs.length; i++) {
	    		event.setEventID( eventIDs[i] );
	    		event.setDbConnection( conn );
	    		event.delete();
	    	}
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
	public static void deleteAllLMProgramEvents(Integer energyCompanyID, Integer programID, java.sql.Connection conn) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs(
					energyCompanyID, programID, conn );
	    	
			com.cannontech.database.data.stars.event.LMProgramEvent event =
					new com.cannontech.database.data.stars.event.LMProgramEvent();
			
			for (int i = 0; i < eventIDs.length; i++) {
				event.setEventID( eventIDs[i] );
				event.setDbConnection( conn );
				event.delete();
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}
    
	public static LMProgramEvent[] getAllLMProgramEvents(Integer accountID, java.sql.Connection conn) {
		try {
			Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getAllLMProgramEventIDs( accountID, conn );
			com.cannontech.database.data.stars.event.LMProgramEvent[] events =
					new com.cannontech.database.data.stars.event.LMProgramEvent[ eventIDs.length ];
	        
			for (int i = 0; i < events.length; i++) {
				events[i] = new com.cannontech.database.data.stars.event.LMProgramEvent();
				events[i].setEventID( eventIDs[i] );
				events[i].setDbConnection( conn );
				events[i].retrieve();
			}
	        
			return events;
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
    	
		return null;
	}
    
    public static LMProgramEvent[] getLMProgramEvents(Integer accountID, Integer programID) {
    	try {
	        Integer[] eventIDs = com.cannontech.database.db.stars.event.LMProgramEvent.getLMProgramEventIDs( accountID, programID );
	        com.cannontech.database.data.stars.event.LMProgramEvent[] events =
	        		new com.cannontech.database.data.stars.event.LMProgramEvent[ eventIDs.length ];
	        
	        for (int i = 0; i < events.length; i++) {
		    	events[i] = new com.cannontech.database.data.stars.event.LMProgramEvent();
		    	events[i].setEventID( eventIDs[i] );
		    	events[i] = (com.cannontech.database.data.stars.event.LMProgramEvent)
		    			Transaction.createTransaction( Transaction.RETRIEVE, events[i] ).execute();
	        }
	        
	        return events;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }

/*
 * Commented out since cache is used now
 *     
    public static StarsLMProgramHistory getStarsLMProgramHistory(Integer accountID, Integer programID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        LMProgramEvent[] events = LMProgramEvent.getAllLMProgramEvents( accountID, programID );
	        StarsLMProgramHistory progHist = new StarsLMProgramHistory();
	        
	        for (int i = 0; i < events.length; i++) {
	        	StarsLMProgramEvent progEvent = new StarsLMProgramEvent();
	        	progEvent.setEventAction( events[i].getAction().getEntryText() );
	        	progEvent.setYukonDefinition( events[i].getAction().getYukonDefinition() );
	        	progEvent.setEventDateTime( events[i].getLMCustomerEventBase().getEventDateTime() );
	        	progEvent.setNotes( events[i].getLMCustomerEventBase().getNotes() );
	        	progHist.addStarsLMProgramEvent( progEvent );
	        }
	        
	        return progHist;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return null;
    }
*/    
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