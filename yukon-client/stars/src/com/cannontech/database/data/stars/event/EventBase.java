package com.cannontech.database.data.stars.event;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: EventBase.java</p>
 * <p>Description: </p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @version 1.0
 */
public class EventBase extends DBPersistent {
	
	private com.cannontech.database.db.stars.event.EventBase eventBase = null;

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
	 * @return com.cannontech.database.db.stars.event.EventBase
	 */
	public com.cannontech.database.db.stars.event.EventBase getEventBase() {
		if (eventBase == null)
			eventBase = new com.cannontech.database.db.stars.event.EventBase();
		return eventBase;
	}

	/**
	 * Sets the eventBase.
	 * @param eventBase The eventBase to set
	 */
	public void setEventBase(
		com.cannontech.database.db.stars.event.EventBase eventBase) {
		this.eventBase = eventBase;
	}
}
