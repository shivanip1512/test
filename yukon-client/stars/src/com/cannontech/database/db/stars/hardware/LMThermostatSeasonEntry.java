package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatSeasonEntry extends DBPersistent {
	
	private Integer seasonID = new Integer(LMThermostatSeason.NONE_INT);
	private Integer timeOfWeekID = new Integer(com.cannontech.database.db.stars.CustomerListEntry.NONE_INT);
	private Integer startTime = new Integer(0);
	private Integer temperature = new Integer(0);
	
	public static final String TABLE_NAME = "LMThermostatSeasonEntry";
			
	public LMThermostatSeasonEntry() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getSeasonID(), getTimeOfWeekID(), getStartTime(), getTemperature()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		throw new SQLException( getClass() + " doesn't support this operation" );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		throw new SQLException( getClass() + " doesn't support this operation" );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		throw new SQLException( getClass() + " doesn't support this operation" );
	}
	
	public static LMThermostatSeasonEntry[] getAllLMThermostatSeasonEntries(Integer seasonID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE SeasonID = " + seasonID.toString()
				   + " ORDER BY TimeOfWeekID, StartTime";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			LMThermostatSeasonEntry[] entries = new LMThermostatSeasonEntry[ stmt.getRowCount() ];
			
			for (int i = 0; i < entries.length; i++) {
				Object[] row = stmt.getRow(i);
				entries[i] = new LMThermostatSeasonEntry();
				
				entries[i].setSeasonID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				entries[i].setTimeOfWeekID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				entries[i].setStartTime( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				entries[i].setTemperature( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
			}
			
			return entries;
		}
		catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
	public Integer getTemperature() {
		return temperature;
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
	 * @param temperature The temperature to set
	 */
	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	/**
	 * Sets the timeOfWeekID.
	 * @param timeOfWeekID The timeOfWeekID to set
	 */
	public void setTimeOfWeekID(Integer timeOfWeekID) {
		this.timeOfWeekID = timeOfWeekID;
	}

}
