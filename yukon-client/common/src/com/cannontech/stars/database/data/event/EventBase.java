package com.cannontech.stars.database.data.event;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: EventBase.java</p>
 * <p>Description: </p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @version 1.0
 */
public class EventBase extends DBPersistent {
	
	private com.cannontech.stars.database.db.event.EventBase eventBase = null;
    
    private String actionText;
    private String userName;
    private int actionYukDefID;
    
	public void setEventID(Integer eventID) {
		getEventBase().setEventID( eventID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getEventBase().setDbConnection(conn);
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getEventBase().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		getEventBase().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getEventBase().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getEventBase().update();
	}

	/**
	 * Returns the eventBase.
	 * @return com.cannontech.stars.database.db.event.EventBase
	 */
	public com.cannontech.stars.database.db.event.EventBase getEventBase() {
		if (eventBase == null)
			eventBase = new com.cannontech.stars.database.db.event.EventBase();
		return eventBase;
	}

	/**
	 * Sets the eventBase.
	 * @param eventBase The eventBase to set
	 */
	public void setEventBase(
		com.cannontech.stars.database.db.event.EventBase eventBase) {
		this.eventBase = eventBase;
	}
    
    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getActionYukDefID() {
        return actionYukDefID;
    }

    public void setActionYukDefID(int actionYukDefID) {
        this.actionYukDefID = actionYukDefID;
    }
}
