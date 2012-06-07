package com.cannontech.stars.database.data.event;

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
	
	private com.cannontech.stars.database.db.event.LMCustomerEventBase lmCustomerEventBase = null;
	private Integer energyCompanyID = null;

	public void setEventID(Integer eventID) {
		getLMCustomerEventBase().setEventID( eventID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getLMCustomerEventBase().setDbConnection(conn);
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
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMCustomerEventBase().update();
	}

	/**
	 * Returns the lmCustomerEventBase.
	 * @return com.cannontech.stars.database.db.event.LMCustomerEventBase
	 */
	public com.cannontech.stars.database.db.event.LMCustomerEventBase getLMCustomerEventBase() {
		if (lmCustomerEventBase == null)
			lmCustomerEventBase = new com.cannontech.stars.database.db.event.LMCustomerEventBase();
		return lmCustomerEventBase;
	}

	/**
	 * Sets the lmCustomerEventBase.
	 * @param lmCustomerEventBase The lmCustomerEventBase to set
	 */
	public void setLMCustomerEventBase(
		com.cannontech.stars.database.db.event.LMCustomerEventBase lmCustomerEventBase) {
		this.lmCustomerEventBase = lmCustomerEventBase;
	}
	
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}
