package com.cannontech.database.data.stars.event;

import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;

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
    
    public static LMHardwareEvent[] getAllLMHardwareEvents(Integer invID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        com.cannontech.database.db.stars.event.LMHardwareEvent[] eventDBs =
	        		com.cannontech.database.db.stars.event.LMHardwareEvent.getAllLMHardwareEvents( invID, conn );
	        com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
	        		new com.cannontech.database.data.stars.event.LMHardwareEvent[ eventDBs.length ];
	        
	        for (int i = 0; i < events.length; i++) {
	        	events[i] = new com.cannontech.database.data.stars.event.LMHardwareEvent();
	        	events[i].setDbConnection(conn);
	        	events[i].setEventID( eventDBs[i].getEventID() );
	        	events[i].retrieve();
	        }
	        
	        return events;
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
    
    public static StarsLMHardwareHistory getStarsLMHardwareHistory(Integer invID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        LMHardwareEvent[] events = LMHardwareEvent.getAllLMHardwareEvents( invID );
	        StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
	        
	        for (int i = 0; i < events.length; i++) {
	        	StarsLMHardwareEvent hwEvent = new StarsLMHardwareEvent();
	        	hwEvent.setEventAction( events[i].getAction().getEntryText() );
	        	hwEvent.setYukonDefinition( events[i].getAction().getYukonDefinition() );
	        	hwEvent.setEventDateTime( events[i].getLMCustomerEventBase().getEventDateTime() );
	        	hwEvent.setNotes( events[i].getLMCustomerEventBase().getNotes() );
	        	hwHist.addStarsLMHardwareEvent( hwEvent );
	        }
	        
	        return hwHist;
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
    
    public static LMHardwareEvent getLastLMHardwareEvent(Integer invID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
	        com.cannontech.database.db.stars.event.LMHardwareEvent eventDB =
	        		com.cannontech.database.db.stars.event.LMHardwareEvent.getLastLMHardwareEvent( invID, conn );
	        if (eventDB == null) return null;
	        
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
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