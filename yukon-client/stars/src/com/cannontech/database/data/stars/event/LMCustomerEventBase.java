package com.cannontech.database.data.stars.event;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: LMCustomerEventBase.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 11:03:28 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LMCustomerEventBase extends DBPersistent {
	
	private com.cannontech.database.db.stars.event.LMCustomerEventBase lmCustomerEventBase = null;
	private com.cannontech.database.db.stars.CustomerListEntry eventType = null;
	private com.cannontech.database.db.stars.CustomerListEntry action = null;
	
	private Integer energyCompanyID = null;

	public void setEventID(Integer eventID) {
		getLMCustomerEventBase().setEventID( eventID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getLMCustomerEventBase().setDbConnection(conn);
		getEventType().setDbConnection(conn);
		getAction().setDbConnection(conn);
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (energyCompanyID == null)
			throw new SQLException("Add: setEnergyCompanyID() must be called before this function");
		
		getLMCustomerEventBase().add();
		
		// Add to the mapping table
		Object[] addValues = {
			energyCompanyID,
			getLMCustomerEventBase().getEventID()
		};
		add("ECToLMCustomerEventMapping", addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		// Delete from the mapping table
		delete("ECToLMCustomerEventMapping", "EventID", getLMCustomerEventBase().getEventID());
		
		getLMCustomerEventBase().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMCustomerEventBase().retrieve();
		
		getEventType().setEntryID( getLMCustomerEventBase().getEventTypeID() );
		getEventType().retrieve();
		
		getAction().setEntryID( getLMCustomerEventBase().getActionID() );
		getAction().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMCustomerEventBase().update();
	}

	/**
	 * Returns the action.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getAction() {
		if (action == null)
			action = new com.cannontech.database.db.stars.CustomerListEntry();
		return action;
	}

	/**
	 * Returns the eventType.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getEventType() {
		if (eventType == null)
			eventType = new com.cannontech.database.db.stars.CustomerListEntry();
		return eventType;
	}

	/**
	 * Returns the lmCustomerEventBase.
	 * @return com.cannontech.database.db.stars.event.LMCustomerEventBase
	 */
	public com.cannontech.database.db.stars.event.LMCustomerEventBase getLMCustomerEventBase() {
		if (lmCustomerEventBase == null)
			lmCustomerEventBase = new com.cannontech.database.db.stars.event.LMCustomerEventBase();
		return lmCustomerEventBase;
	}

	/**
	 * Sets the action.
	 * @param action The action to set
	 */
	public void setAction(
		com.cannontech.database.db.stars.CustomerListEntry action) {
		this.action = action;
	}

	/**
	 * Sets the eventType.
	 * @param eventType The eventType to set
	 */
	public void setEventType(
		com.cannontech.database.db.stars.CustomerListEntry eventType) {
		this.eventType = eventType;
	}

	/**
	 * Sets the lmCustomerEventBase.
	 * @param lmCustomerEventBase The lmCustomerEventBase to set
	 */
	public void setLMCustomerEventBase(
		com.cannontech.database.db.stars.event.LMCustomerEventBase lmCustomerEventBase) {
		this.lmCustomerEventBase = lmCustomerEventBase;
	}
	
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}
