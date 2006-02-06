package com.cannontech.database.data.stars.event;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EventInventory extends EventBase {

    private com.cannontech.database.db.stars.event.EventInventory eventInventory = null;

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
	 * @return com.cannontech.database.db.stars.event.EventInventory
	 */
	public com.cannontech.database.db.stars.event.EventInventory getEventInventory() {
		if (eventInventory == null)
			eventInventory = new com.cannontech.database.db.stars.event.EventInventory();
		return eventInventory;
	}

	/**
	 * Sets the eventInventory.
	 * @param eventInventory The eventInventory to set
	 */
	public void setEventInventory(
		com.cannontech.database.db.stars.event.EventInventory eventInventory) {
		this.eventInventory = eventInventory;
	}

}