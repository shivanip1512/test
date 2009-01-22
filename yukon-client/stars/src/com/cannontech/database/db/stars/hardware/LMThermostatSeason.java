package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
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
public class LMThermostatSeason extends DBPersistent {
	
	public static final int NONE_INT = 0;
	
	private Integer seasonID = null;
	private Integer scheduleID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer webConfigurationID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Date coolStartDate = new Date(0);
	private Date heatStartDate = new Date(0);
	
	public static final String[] SETTER_COLUMNS = {
		"ScheduleID", "WebConfigurationID", "CoolStartDate", "HeatStartDate"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "SeasonID" };
	
	public static final String TABLE_NAME = "LMThermostatSeason";
	
	private static NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
			
	public LMThermostatSeason() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException {
		if (getSeasonID() == null)
			setSeasonID( nextValueHelper.getNextValue(TABLE_NAME) );
			
		Object[] addValues = {
			getSeasonID(), getScheduleID(), getWebConfigurationID(),
			getCoolStartDate(), getHeatStartDate()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException {
		Object[] constraintValues = { getSeasonID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException {
		Object[] constraintValues = { getSeasonID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setScheduleID( (Integer) results[0] );
			setWebConfigurationID( (Integer) results[1] );
			
			Object coolStart = results[2];
			if(coolStart != null) {
				setCoolStartDate( new Date(((java.sql.Timestamp) coolStart).getTime()) );
			}
			Object heatStart = results[3];
			if(heatStart != null) {
				setHeatStartDate( new Date(((java.sql.Timestamp) heatStart).getTime()) );
			}
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
			getScheduleID(), getWebConfigurationID(), getCoolStartDate(), getHeatStartDate()
		};
		Object[] constraintValues = { getSeasonID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static LMThermostatSeason[] getAllLMThermostatSeasons(int scheduleID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ScheduleID = " + scheduleID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			LMThermostatSeason[] seasons = new LMThermostatSeason[ stmt.getRowCount() ];
			
			for (int i = 0; i < seasons.length; i++) {
				Object[] row = stmt.getRow(i);
				seasons[i] = new LMThermostatSeason();
				
				seasons[i].setSeasonID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				seasons[i].setScheduleID(  new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				seasons[i].setWebConfigurationID(  new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				seasons[i].setCoolStartDate( (Date) row[3] );
				seasons[i].setHeatStartDate( (Date) row[4] );
			}
			
			return seasons;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * Returns the scheduleID.
	 * @return Integer
	 */
	public Integer getScheduleID() {
		return scheduleID;
	}

	/**
	 * Returns the seasonID.
	 * @return Integer
	 */
	public Integer getSeasonID() {
		return seasonID;
	}

	/**
	 * Returns the startDate.
	 * @return Date
	 */
	public Date getCoolStartDate() {
		return coolStartDate;
	}
	
	public Date getHeatStartDate() {
		return heatStartDate;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return Integer
	 */
	public Integer getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the scheduleID.
	 * @param scheduleID The scheduleID to set
	 */
	public void setScheduleID(Integer scheduleID) {
		this.scheduleID = scheduleID;
	}

	/**
	 * Sets the seasonID.
	 * @param seasonID The seasonID to set
	 */
	public void setSeasonID(Integer seasonID) {
		this.seasonID = seasonID;
	}

	/**
	 * Sets the startDate.
	 * @param coolStartDate The startDate to set
	 */
	public void setCoolStartDate(Date coolStartDate) {
		this.coolStartDate = coolStartDate;
	}
	
	public void setHeatStartDate(Date heatStartDate) {
		this.heatStartDate = heatStartDate;
	}

	/**
	 * Sets the webConfigurationID.
	 * @param webConfigurationID The webConfigurationID to set
	 */
	public void setWebConfigurationID(Integer webConfigurationID) {
		this.webConfigurationID = webConfigurationID;
	}

}
