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
public class ApplianceWaterHeater extends DBPersistent {

	private Integer applianceID = null;
	private Integer numberOfGallonsID = new Integer( CtiUtilities.NONE_ID );
	private Integer energySourceID = new Integer( CtiUtilities.NONE_ID );
	private Integer numberOfElements = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"NumberOfGallonsID", "EnergySourceID", "NumberOfElements"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceWaterHeater";
	
	public ApplianceWaterHeater() {
		super();
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getNumberOfGallonsID(), getEnergySourceID(), getNumberOfElements()
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
			setNumberOfGallonsID( (Integer) results[0] );
			setEnergySourceID( (Integer) results[1] );
			setNumberOfElements( (Integer) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getNumberOfGallonsID(), getEnergySourceID(), getNumberOfElements()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
    
    public static ApplianceWaterHeater getApplianceWaterHeater(Integer appID) {
    	String sql = "SELECT ApplianceID, NumberOfGallonsID, EnergySourceID, NumberOfElements " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceWaterHeater app = new ApplianceWaterHeater();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setNumberOfGallonsID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setEnergySourceID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		app.setNumberOfElements( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		
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
	 * Returns the energySourceID.
	 * @return Integer
	 */
	public Integer getEnergySourceID() {
		return energySourceID;
	}

	/**
	 * Returns the numberOfElements.
	 * @return Integer
	 */
	public Integer getNumberOfElements() {
		return numberOfElements;
	}

	/**
	 * Returns the numberOfGallonsID.
	 * @return Integer
	 */
	public Integer getNumberOfGallonsID() {
		return numberOfGallonsID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the energySourceID.
	 * @param energySourceID The energySourceID to set
	 */
	public void setEnergySourceID(Integer energySourceID) {
		this.energySourceID = energySourceID;
	}

	/**
	 * Sets the numberOfElements.
	 * @param numberOfElements The numberOfElements to set
	 */
	public void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * Sets the numberOfGallonsID.
	 * @param numberOfGallonsID The numberOfGallonsID to set
	 */
	public void setNumberOfGallonsID(Integer numberOfGallonsID) {
		this.numberOfGallonsID = numberOfGallonsID;
	}

}
