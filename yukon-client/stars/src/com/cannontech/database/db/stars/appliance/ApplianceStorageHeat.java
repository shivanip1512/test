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
public class ApplianceStorageHeat extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer storageTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer peakKWCapacity = new Integer(0);
	private Integer hoursToRecharge = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"StorageTypeID", "PeakKWCapacity", "HoursToRecharge"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceStorageHeat";
	
	public ApplianceStorageHeat() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getStorageTypeID(), getPeakKWCapacity(), getHoursToRecharge()
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
			setStorageTypeID( (Integer) results[0] );
			setPeakKWCapacity( (Integer) results[1] );
			setHoursToRecharge( (Integer) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getStorageTypeID(), getPeakKWCapacity(), getHoursToRecharge()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
    
    public static ApplianceStorageHeat getApplianceStorageHeat(Integer appID) {
    	String sql = "SELECT ApplianceID, StorageTypeID, PeakKWCapacity, HoursToRecharge " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceStorageHeat app = new ApplianceStorageHeat();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setStorageTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setPeakKWCapacity( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		app.setHoursToRecharge( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		
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
	 * Returns the hoursToRecharge.
	 * @return Integer
	 */
	public Integer getHoursToRecharge() {
		return hoursToRecharge;
	}

	/**
	 * Returns the peakKWCapacity.
	 * @return Integer
	 */
	public Integer getPeakKWCapacity() {
		return peakKWCapacity;
	}

	/**
	 * Returns the storageTypeID.
	 * @return Integer
	 */
	public Integer getStorageTypeID() {
		return storageTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the hoursToRecharge.
	 * @param hoursToRecharge The hoursToRecharge to set
	 */
	public void setHoursToRecharge(Integer hoursToRecharge) {
		this.hoursToRecharge = hoursToRecharge;
	}

	/**
	 * Sets the peakKWCapacity.
	 * @param peakKWCapacity The peakKWCapacity to set
	 */
	public void setPeakKWCapacity(Integer peakKWCapacity) {
		this.peakKWCapacity = peakKWCapacity;
	}

	/**
	 * Sets the storageTypeID.
	 * @param storageTypeID The storageTypeID to set
	 */
	public void setStorageTypeID(Integer storageTypeID) {
		this.storageTypeID = storageTypeID;
	}

}
