package com.cannontech.database.data.stars.event;


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
    
    public static LMProgramEvent getLastProgramEvent(Integer accountID, Integer programID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        com.cannontech.database.db.stars.event.LMProgramEvent eventDB =
	        		com.cannontech.database.db.stars.event.LMProgramEvent.getLastProgramEvent( accountID, programID, conn );
	        if (eventDB == null) return null;
	        
        	com.cannontech.database.data.stars.event.LMProgramEvent event = new com.cannontech.database.data.stars.event.LMProgramEvent();
        	event.setDbConnection(conn);
        	event.setEventID( eventDB.getEventID() );
        	event.retrieve();
	        
	        return event;
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