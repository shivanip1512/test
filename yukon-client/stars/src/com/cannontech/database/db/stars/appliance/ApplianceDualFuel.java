package com.cannontech.database.db.stars.appliance;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ApplianceDualFuel extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer switchOverTypeID = new Integer( CtiUtilities.NONE_ID );
	private Integer secondaryKWCapacity = new Integer(0);
	private Integer secondaryEnergySourceID = new Integer( CtiUtilities.NONE_ID );
	
	public static final String[] SETTER_COLUMNS = {
		"SwitchOverTypeID", "SecondaryKWCapacity", "SecondaryEnergySourceID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceDualFuel";
	
	public ApplianceDualFuel() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getSwitchOverTypeID(), getSecondaryKWCapacity(), getSecondaryEnergySourceID()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getApplianceID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getApplianceID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setSwitchOverTypeID( (Integer) results[0] );
			setSecondaryKWCapacity( (Integer) results[1] );
			setSecondaryEnergySourceID( (Integer) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getSwitchOverTypeID(), getSecondaryKWCapacity(), getSecondaryEnergySourceID()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		
	}
    
    public static ApplianceDualFuel getApplianceDualFuel(Integer appID) {
    	String sql = "SELECT ApplianceID, SwitchOverTypeID, SecondaryKWCapacity, SecondaryEnergySourceID " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceDualFuel app = new ApplianceDualFuel();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setSwitchOverTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setSecondaryKWCapacity( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		app.setSecondaryEnergySourceID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		
    		return app;
    	}
    	catch (Exception e) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }

	/**
	 * Returns the applianceID.
	 * @return Integer
	 */
	public Integer getApplianceID() {
		return applianceID;
	}

	/**
	 * Returns the secondaryEnergySourceID.
	 * @return Integer
	 */
	public Integer getSecondaryEnergySourceID() {
		return secondaryEnergySourceID;
	}

	/**
	 * Returns the secondaryKWCapacity.
	 * @return Integer
	 */
	public Integer getSecondaryKWCapacity() {
		return secondaryKWCapacity;
	}

	/**
	 * Returns the swithOverTypeID.
	 * @return Integer
	 */
	public Integer getSwitchOverTypeID() {
		return switchOverTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the secondaryEnergySourceID.
	 * @param secondaryEnergySourceID The secondaryEnergySourceID to set
	 */
	public void setSecondaryEnergySourceID(Integer secondaryEnergySourceID) {
		this.secondaryEnergySourceID = secondaryEnergySourceID;
	}

	/**
	 * Sets the secondaryKWCapacity.
	 * @param secondaryKWCapacity The secondaryKWCapacity to set
	 */
	public void setSecondaryKWCapacity(Integer secondaryKWCapacity) {
		this.secondaryKWCapacity = secondaryKWCapacity;
	}

	/**
	 * Sets the swithOverTypeID.
	 * @param swithOverTypeID The swithOverTypeID to set
	 */
	public void setSwitchOverTypeID(Integer switchOverTypeID) {
		this.switchOverTypeID = switchOverTypeID;
	}

}
