package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatSeasonEntry extends DBPersistent {
	
	public static final int NONE_INT = 0;
	
	private Integer entryID = null;
	private Integer seasonID = new Integer(LMThermostatSeason.NONE_INT);
	private Integer timeOfWeekID = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
	private Integer startTime = new Integer(0);
	private Integer coolTemperature = new Integer(0);
	private Integer heatTemperature = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"SeasonID", "TimeOfWeekID", "StartTime", "CoolTemperature", "HeatTemperature"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "EntryID" };
	
	public static final String TABLE_NAME = "LMThermostatSeasonEntry";
	
	private static NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
			
	public LMThermostatSeasonEntry() {
		super();
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException {
		if (getEntryID() == null)
			setEntryID( nextValueHelper.getNextValue(TABLE_NAME) );
			
		Object[] addValues = {
			getEntryID(), getSeasonID(), getTimeOfWeekID(), getStartTime(), getCoolTemperature(), 
			getHeatTemperature()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException {
		Object[] constraintValues = { getEntryID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException {
		Object[] constraintValues = { getEntryID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setSeasonID( (Integer) results[0] );
			setTimeOfWeekID( (Integer) results[1] );
			setStartTime( (Integer) results[2] );
			setCoolTemperature( (Integer) results[3] );
			setHeatTemperature( (Integer) results[4] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	@Override
    public void update() throws SQLException {
		Object[] setValues = {
			getSeasonID(), getTimeOfWeekID(), getStartTime(), getCoolTemperature(), getHeatTemperature()
		};
		Object[] constraintValues = { getEntryID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static LMThermostatSeasonEntry[] getAllLMThermostatSeasonEntries(Integer seasonID, java.sql.Connection conn) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE SeasonID = " + seasonID.toString()
				   + " ORDER BY EntryID";
		com.cannontech.database.SqlStatement stmt =
				new com.cannontech.database.SqlStatement( sql, conn );
		
		try {
			stmt.execute();
			LMThermostatSeasonEntry[] entries = new LMThermostatSeasonEntry[ stmt.getRowCount() ];
			
			for (int i = 0; i < entries.length; i++) {
				Object[] row = stmt.getRow(i);
				entries[i] = new LMThermostatSeasonEntry();
				
				entries[i].setEntryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				entries[i].setSeasonID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				entries[i].setTimeOfWeekID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				entries[i].setStartTime( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				
				
				Object coolTemp = row[4];
				if(coolTemp != null ) {
					entries[i].setCoolTemperature( new Integer(((java.math.BigDecimal) coolTemp).intValue()) );
				}
				
				Object heatTemp = row[5];
				if(heatTemp != null) {
					entries[i].setHeatTemperature( new Integer(((java.math.BigDecimal) heatTemp).intValue()) );
				}
			}
			
			return entries;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static void deleteAllLMThermostatSeasonEntries(Integer seasonID) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE SeasonID = " + seasonID.toString();
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static void deleteAllLMThermostatSeasonEntries(Integer seasonID, Integer towID) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE SeasonID = " + seasonID.toString()
				   + " AND TimeOfWeekID = " + towID.toString();
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

	/**
	 * Returns the seasonID.
	 * @return Integer
	 */
	public Integer getSeasonID() {
		return seasonID;
	}

	/**
	 * Returns the startTime.
	 * @return Integer
	 */
	public Integer getStartTime() {
		return startTime;
	}

	/**
	 * Returns the temperature.
	 * @return Integer
	 */
	public Integer getCoolTemperature() {
		return coolTemperature;
	}
	
	public Integer getHeatTemperature() {
		return heatTemperature;
	}

	/**
	 * Returns the timeOfWeekID.
	 * @return Integer
	 */
	public Integer getTimeOfWeekID() {
		return timeOfWeekID;
	}

	/**
	 * Sets the seasonID.
	 * @param seasonID The seasonID to set
	 */
	public void setSeasonID(Integer seasonID) {
		this.seasonID = seasonID;
	}

	/**
	 * Sets the startTime.
	 * @param startTime The startTime to set
	 */
	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	/**
	 * Sets the temperature.
	 * @param coolTemperature The temperature to set
	 */
	public void setCoolTemperature(Integer coolTemperature) {
		this.coolTemperature = coolTemperature;
	}
	
	public void setHeatTemperature(Integer heatTemperature) {
		this.heatTemperature = heatTemperature;
	}

	/**
	 * Sets the timeOfWeekID.
	 * @param timeOfWeekID The timeOfWeekID to set
	 */
	public void setTimeOfWeekID(Integer timeOfWeekID) {
		this.timeOfWeekID = timeOfWeekID;
	}

	/**
	 * Returns the entryID.
	 * @return Integer
	 */
	public Integer getEntryID() {
		return entryID;
	}

	/**
	 * Sets the entryID.
	 * @param entryID The entryID to set
	 */
	public void setEntryID(Integer entryID) {
		this.entryID = entryID;
	}

}
