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
public class ApplianceGrainDryer extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer dryerTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer binSizeID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer blowerEnergySourceID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer blowerHorsePowerID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer blowerHeatSourceID = new Integer( CtiUtilities.NONE_ZERO_ID );
	
	public static final String[] SETTER_COLUMNS = {
		"DryerTypeID", "BinSizeID", "BlowerEnergySourceID", "BlowerHorsePowerID", "BlowerHeatSourceID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceGrainDryer";
	
	public ApplianceGrainDryer() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getDryerTypeID(), getBinSizeID(), getBlowerEnergySourceID(),
			getBlowerHorsePowerID(), getBlowerHeatSourceID()
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
			setDryerTypeID( (Integer) results[0] );
			setBinSizeID( (Integer) results[1] );
			setBlowerEnergySourceID( (Integer) results[2] );
			setBlowerHorsePowerID( (Integer) results[3] );
			setBlowerHeatSourceID( (Integer) results[4] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getDryerTypeID(), getBinSizeID(), getBlowerEnergySourceID(),
			getBlowerHorsePowerID(), getBlowerHeatSourceID()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
    
    public static ApplianceGrainDryer getApplianceGrainDryer(Integer appID) {
    	String sql = "SELECT ApplianceID, DryerTypeID, BinSizeID, BlowerEnergySourceID, BlowerHorsePowerID, BlowerHeatSourceID " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceGrainDryer app = new ApplianceGrainDryer();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setDryerTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setBinSizeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		app.setBlowerEnergySourceID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		app.setBlowerHorsePowerID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
    		app.setBlowerHeatSourceID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
    		
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
	 * Returns the binSizeID.
	 * @return Integer
	 */
	public Integer getBinSizeID() {
		return binSizeID;
	}

	/**
	 * Returns the blowerEnergySourceID.
	 * @return Integer
	 */
	public Integer getBlowerEnergySourceID() {
		return blowerEnergySourceID;
	}

	/**
	 * Returns the blowerHeatSourceID.
	 * @return Integer
	 */
	public Integer getBlowerHeatSourceID() {
		return blowerHeatSourceID;
	}

	/**
	 * Returns the blowerHorsePowerID.
	 * @return Integer
	 */
	public Integer getBlowerHorsePowerID() {
		return blowerHorsePowerID;
	}

	/**
	 * Returns the dryerTypeID.
	 * @return Integer
	 */
	public Integer getDryerTypeID() {
		return dryerTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the binSizeID.
	 * @param binSizeID The binSizeID to set
	 */
	public void setBinSizeID(Integer binSizeID) {
		this.binSizeID = binSizeID;
	}

	/**
	 * Sets the blowerEnergySourceID.
	 * @param blowerEnergySourceID The blowerEnergySourceID to set
	 */
	public void setBlowerEnergySourceID(Integer blowerEnergySourceID) {
		this.blowerEnergySourceID = blowerEnergySourceID;
	}

	/**
	 * Sets the blowerHeatSourceID.
	 * @param blowerHeatSourceID The blowerHeatSourceID to set
	 */
	public void setBlowerHeatSourceID(Integer blowerHeatSourceID) {
		this.blowerHeatSourceID = blowerHeatSourceID;
	}

	/**
	 * Sets the blowerHorsePowerID.
	 * @param blowerHorsePowerID The blowerHorsePowerID to set
	 */
	public void setBlowerHorsePowerID(Integer blowerHorsePowerID) {
		this.blowerHorsePowerID = blowerHorsePowerID;
	}

	/**
	 * Sets the dryerTypeID.
	 * @param dryerTypeID The dryerTypeID to set
	 */
	public void setDryerTypeID(Integer dryerTypeID) {
		this.dryerTypeID = dryerTypeID;
	}

}
