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
public class ApplianceGenerator extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer transferSwitchTypeID = new Integer( CtiUtilities.NONE_ID );
	private Integer transferSwitchMfgID = new Integer( CtiUtilities.NONE_ID );
	private Integer peakKWCapacity = new Integer(0);
	private Integer fuelCapGallons = new Integer(0);
	private Integer startDelaySeconds = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"TransferSwitchTypeID", "TransferSwitchMfgID", "PeakKWCapacity",
		"FuelCapGallons", "StartDelaySeconds"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceGenerator";
	
	public ApplianceGenerator() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getTransferSwitchTypeID(), getTransferSwitchMfgID(),
			getPeakKWCapacity(), getFuelCapGallons(), getStartDelaySeconds()
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
			setTransferSwitchTypeID( (Integer) results[0] );
			setTransferSwitchMfgID( (Integer) results[1] );
			setPeakKWCapacity( (Integer) results[2] );
			setFuelCapGallons( (Integer) results[3] );
			setStartDelaySeconds( (Integer) results[4] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getTransferSwitchTypeID(), getTransferSwitchMfgID(), getPeakKWCapacity(),
			getFuelCapGallons(), getStartDelaySeconds()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
    
    public static ApplianceGenerator getApplianceGenerator(Integer appID) {
    	String sql = "SELECT ApplianceID, TransferSwitchTypeID, TransferSwitchMfgID, PeakKWCapacity, FuelCapGallons, StartDelaySeconds " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceGenerator app = new ApplianceGenerator();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setTransferSwitchTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setTransferSwitchMfgID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		app.setPeakKWCapacity( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		app.setFuelCapGallons( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
    		app.setStartDelaySeconds( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
    		
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
	 * Returns the fuelCapGallons.
	 * @return Integer
	 */
	public Integer getFuelCapGallons() {
		return fuelCapGallons;
	}

	/**
	 * Returns the peakKWCapacity.
	 * @return Integer
	 */
	public Integer getPeakKWCapacity() {
		return peakKWCapacity;
	}

	/**
	 * Returns the startDelaySeconds.
	 * @return Integer
	 */
	public Integer getStartDelaySeconds() {
		return startDelaySeconds;
	}

	/**
	 * Returns the transferSwitchMfgID.
	 * @return Integer
	 */
	public Integer getTransferSwitchMfgID() {
		return transferSwitchMfgID;
	}

	/**
	 * Returns the transferSwitchTypeID.
	 * @return Integer
	 */
	public Integer getTransferSwitchTypeID() {
		return transferSwitchTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the fuelCapGallons.
	 * @param fuelCapGallons The fuelCapGallons to set
	 */
	public void setFuelCapGallons(Integer fuelCapGallons) {
		this.fuelCapGallons = fuelCapGallons;
	}

	/**
	 * Sets the peakKWCapacity.
	 * @param peakKWCapacity The peakKWCapacity to set
	 */
	public void setPeakKWCapacity(Integer peakKWCapacity) {
		this.peakKWCapacity = peakKWCapacity;
	}

	/**
	 * Sets the startDelaySeconds.
	 * @param startDelaySeconds The startDelaySeconds to set
	 */
	public void setStartDelaySeconds(Integer startDelaySeconds) {
		this.startDelaySeconds = startDelaySeconds;
	}

	/**
	 * Sets the transferSwitchMfgID.
	 * @param transferSwitchMfgID The transferSwitchMfgID to set
	 */
	public void setTransferSwitchMfgID(Integer transferSwitchMfgID) {
		this.transferSwitchMfgID = transferSwitchMfgID;
	}

	/**
	 * Sets the transferSwitchTypeID.
	 * @param transferSwitchTypeID The transferSwitchTypeID to set
	 */
	public void setTransferSwitchTypeID(Integer transferSwitchTypeID) {
		this.transferSwitchTypeID = transferSwitchTypeID;
	}

}
