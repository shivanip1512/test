package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

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
	private Date startDate = new Date(0);
	private Integer displayOrder = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"ScheduleID", "WebConfigurationID", "StartDate", "DisplayOrder"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "SeasonID" };
	
	public static final String TABLE_NAME = "LMThermostatSeason";
	
	public static final String GET_NEXT_SEASON_ID_SQL =
			"SELECT MAX(SeasonID) FROM " + TABLE_NAME;
			
	public LMThermostatSeason() {
		super();
	}
	
	public final Integer getNextSeasonID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextSeasonID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_SEASON_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextSeasonID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
        }

        return new Integer( nextSeasonID );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getSeasonID() == null)
			setSeasonID( getNextSeasonID() );
			
		Object[] addValues = {
			getSeasonID(), getScheduleID(), getWebConfigurationID(),
			getStartDate(), getDisplayOrder()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getSeasonID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getSeasonID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setScheduleID( (Integer) results[0] );
			setWebConfigurationID( (Integer) results[1] );
			setStartDate( new Date(((java.sql.Timestamp) results[2]).getTime()) );
			setDisplayOrder( (Integer) results[3] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getScheduleID(), getWebConfigurationID(), getStartDate(), getDisplayOrder()
		};
		Object[] constraintValues = { getSeasonID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static LMThermostatSeason[] getAllLMThermostatSeasons(int scheduleID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ScheduleID = " + scheduleID
				   + " ORDER BY DisplayOrder";
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
				seasons[i].setStartDate( (Date) row[3] );
				seasons[i].setDisplayOrder(  new Integer(((java.math.BigDecimal) row[4]).intValue()) );
			}
			
			return seasons;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * Returns the displayOrder.
	 * @return Integer
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
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
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return Integer
	 */
	public Integer getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the displayOrder.
	 * @param displayOrder The displayOrder to set
	 */
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
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
	 * @param startDate The startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the webConfigurationID.
	 * @param webConfigurationID The webConfigurationID to set
	 */
	public void setWebConfigurationID(Integer webConfigurationID) {
		this.webConfigurationID = webConfigurationID;
	}

}
