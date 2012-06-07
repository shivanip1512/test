package com.cannontech.stars.database.db.appliance;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceBase extends DBPersistent {

	public static final int NONE_INT = 0;

	private Integer applianceID = null;
	private Integer accountID = new Integer( com.cannontech.stars.database.db.customer.CustomerAccount.NONE_INT );
	private Integer applianceCategoryID = new Integer( ApplianceCategory.NONE_INT );
	private Integer programID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer yearManufactured = new Integer(0);
	private Integer manufacturerID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer locationID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Double kwCapacity = new Double(0.0);
	private Double efficiencyRating = new Double(0.0);
	private String notes = "";
	private String modelNumber = "";

	public static final String[] SETTER_COLUMNS = {
		"AccountID", "ApplianceCategoryID", "ProgramID", "YearManufactured",
		"ManufacturerID", "LocationID", "KWCapacity", "EfficiencyRating", "Notes", "ModelNumber"
	};

	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

	public static final String TABLE_NAME = "ApplianceBase";

	public static final String[] DEPENDENT_TABLES = {
		ApplianceAirConditioner.TABLE_NAME,
		ApplianceDualFuel.TABLE_NAME,
		ApplianceGenerator.TABLE_NAME,
		ApplianceGrainDryer.TABLE_NAME,
		ApplianceHeatPump.TABLE_NAME,
		ApplianceIrrigation.TABLE_NAME,
		ApplianceStorageHeat.TABLE_NAME,
		ApplianceWaterHeater.TABLE_NAME
	};

	public ApplianceBase() {
		super();
	}

	public static Vector<Integer> getApplianceIDs(Integer accountID, java.sql.Connection conn)
	throws java.sql.SQLException {
		String sql = "SELECT ApplianceID FROM " + TABLE_NAME + " WHERE AccountID = ?";
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
        
		try {
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, accountID.intValue() );
			rset = stmt.executeQuery();
        	
			Vector<Integer> appIDVct = new Vector<Integer>();
			while (rset.next())
				appIDVct.add( new Integer(rset.getInt(1)) );
			return appIDVct;
		}
		finally {
			SqlUtils.close(rset, stmt);
		}
	}

	@Override
    public void delete() throws java.sql.SQLException {
		Object[] constraintValues = { getApplianceID() };

		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	@Override
    public void add() throws java.sql.SQLException {
		if (getApplianceID() == null)
			setApplianceID( getNextApplianceID() );
    		
		Object[] addValues = {
			getApplianceID(), getAccountID(), getApplianceCategoryID(), getProgramID(), getYearManufactured(),
			getManufacturerID(), getLocationID(), getKWCapacity(), getEfficiencyRating(), getNotes(), getModelNumber()
		};

		add( TABLE_NAME, addValues );
	}

	@Override
    public void update() throws java.sql.SQLException {
		Object[] setValues = {
			getAccountID(), getApplianceCategoryID(), getProgramID(), getYearManufactured(),
			getManufacturerID(), getLocationID(), getKWCapacity(), getEfficiencyRating(), getNotes(), getModelNumber()
		};

		Object[] constraintValues = { getApplianceID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	@Override
    public void retrieve() throws java.sql.SQLException {
		Object[] constraintValues = { getApplianceID() };

		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if (results.length == SETTER_COLUMNS.length) {
			setAccountID( (Integer) results[0] );
			setApplianceCategoryID( (Integer) results[1] );
			setProgramID( (Integer) results[2] );
			setYearManufactured( (Integer) results[3] );
			setManufacturerID( (Integer) results[4] );
			setLocationID( (Integer) results[5] );
			setKWCapacity( (Double) results[6] );
			setEfficiencyRating( (Double) results[7] );
			setNotes( (String) results[8] );
			setModelNumber( (String) results[9] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	private Integer getNextApplianceID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }
	
	public static void deleteAppliancesByCategory(int appCatID) {
		String cond = "FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = " + appCatID;
		SqlStatement stmt = new SqlStatement( "", CtiUtilities.getDatabaseAlias() );
		
		try {
			for (int i = 0; i < DEPENDENT_TABLES.length; i++) {
				String sql = "DELETE FROM " + DEPENDENT_TABLES[i] + " WHERE ApplianceID IN (SELECT ApplianceID " + cond + ")";
				stmt.setSQLString( sql );
				stmt.execute();
			}
			
			String sql = "DELETE FROM LMHardwareConfiguration WHERE ApplianceID IN (SELECT ApplianceID " + cond + ")";
			stmt.setSQLString( sql );
			stmt.execute();
			
			stmt.setSQLString( "DELETE " + cond );
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static void resetAppliancesByProgram(int programID) {
		String cond = " WHERE ProgramID = " + programID;
		String sql = "DELETE FROM LMHardwareConfiguration WHERE ApplianceID IN (" +
			"SELECT ApplianceID FROM " + TABLE_NAME + cond + ")";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			
			sql = "UPDATE " + TABLE_NAME + " SET ProgramID = 0" + cond;
			stmt.setSQLString( sql );
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

    public static HashMap<Integer, Integer> getAllInventoryIDsFromApplianceCategory(int appCatID)
    {
        HashMap<Integer, Integer> inventoryIDs = new HashMap<Integer, Integer>();
        
        String stmt = "select InventoryID from LMHardwareConfiguration lmh, ApplianceBase app where lmh.ApplianceID = app.ApplianceID and app.ApplianceCategoryID = " + appCatID;
        
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            @SuppressWarnings("unchecked") List<Integer> ids = jdbcOps.queryForList(stmt, Integer.class);
            for(Integer hardwareID : ids) {
                inventoryIDs.put(hardwareID, hardwareID);
            }
        } 
        catch (IncorrectResultSizeDataAccessException e) {
            return inventoryIDs;
        }
        
        return inventoryIDs;
    }

	public Integer getApplianceID() {
		return applianceID;
	}

	public void setApplianceID(Integer newApplianceID) {
		applianceID = newApplianceID;
	}

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer newAccountID) {
		accountID = newAccountID;
	}

	public Integer getApplianceCategoryID() {
		return applianceCategoryID;
	}

	public void setApplianceCategoryID(Integer newApplianceCategoryID) {
		applianceCategoryID = newApplianceCategoryID;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String newNotes) {
		notes = newNotes;
	}
	/**
	 * Returns the programID.
	 * @return Integer
	 */
	public Integer getProgramID() {
		return programID;
	}

	/**
	 * Sets the programID.
	 * @param programID The programID to set
	 */
	public void setProgramID(Integer programID) {
		this.programID = programID;
	}

	/**
	 * Returns the efficiencyRating.
	 * @return Integer
	 */
	public Double getEfficiencyRating() {
		return efficiencyRating;
	}

	/**
	 * Returns the kwCapacity.
	 * @return Double
	 */
	public Double getKWCapacity() {
		return kwCapacity;
	}

	/**
	 * Returns the locationID.
	 * @return Integer
	 */
	public Integer getLocationID() {
		return locationID;
	}

	/**
	 * Returns the menufacturerID.
	 * @return Integer
	 */
	public Integer getManufacturerID() {
		return manufacturerID;
	}

	/**
	 * Sets the efficiencyRating.
	 * @param efficiencyRating The efficiencyRating to set
	 */
	public void setEfficiencyRating(Double efficiencyRating) {
		this.efficiencyRating = efficiencyRating;
	}

	/**
	 * Sets the kwCapacity.
	 * @param kwCapacity The kwCapacity to set
	 */
	public void setKWCapacity(Double kwCapacity) {
		this.kwCapacity = kwCapacity;
	}

	/**
	 * Sets the locationID.
	 * @param locationID The locationID to set
	 */
	public void setLocationID(Integer locationID) {
		this.locationID = locationID;
	}

	/**
	 * Sets the menufacturerID.
	 * @param menufacturerID The menufacturerID to set
	 */
	public void setManufacturerID(Integer menufacturerID) {
		this.manufacturerID = menufacturerID;
	}

	/**
	 * Returns the yearManufactured.
	 * @return Integer
	 */
	public Integer getYearManufactured() {
		return yearManufactured;
	}

	/**
	 * Sets the yearManufactured.
	 * @param yearManufactured The yearManufactured to set
	 */
	public void setYearManufactured(Integer yearManufactured) {
		this.yearManufactured = yearManufactured;
	}

	/**
	 * Returns the modelNumber.
	 * @return String
	 */
	public String getModelNumber() {
		return modelNumber;
	}

	/**
	 * Sets the modelNumber.
	 * @param modelNumber The modelNumber to set
	 */
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

}