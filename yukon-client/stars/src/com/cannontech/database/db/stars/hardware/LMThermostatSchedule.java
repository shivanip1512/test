/*
 * Created on May 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMThermostatSchedule extends DBPersistent {
	
	private Integer scheduleID = null;
	private String scheduleName = CtiUtilities.STRING_NONE;
	private Integer thermostatTypeID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer accountID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer inventoryID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	public static final String[] SETTER_COLUMNS = {
		"ScheduleName", "ThermostatTypeID", "AccountID", "InventoryID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ScheduleID" };
	
	public static final String TABLE_NAME = "LMThermostatSchedule";
	
	public static final String GET_NEXT_SCHEDULE_ID_SQL =
			"SELECT MAX(ScheduleID) FROM " + TABLE_NAME;
	
	public LMThermostatSchedule() {
		super();
	}
	
	public final Integer getNextScheduleID() {
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		int nextScheduleID = 1;

		try {
			pstmt = getDbConnection().prepareStatement( GET_NEXT_SCHEDULE_ID_SQL );
			rset = pstmt.executeQuery();

			if (rset.next())
				nextScheduleID = rset.getInt(1) + 1;
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

		return new Integer( nextScheduleID );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getScheduleID() == null)
			setScheduleID( getNextScheduleID() );
		
		Object[] addValues = {
			getScheduleID(), getScheduleName(), getThermostatTypeID(),
			getAccountID(), getInventoryID()
		};
		add( TABLE_NAME, addValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getScheduleID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getScheduleID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setScheduleName( (String) results[0] );
			setThermostatTypeID( (Integer) results[1] );
			setAccountID( (Integer) results[2] );
			setInventoryID( (Integer) results[3] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getScheduleName(), getThermostatTypeID(), getAccountID(), getInventoryID()
		};
		Object[] constraintValues = { getScheduleID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static LMThermostatSchedule getThermostatSchedule(int inventoryID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = " + inventoryID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() > 0) {
				Object[] row = stmt.getRow(0);
				LMThermostatSchedule schedule = new LMThermostatSchedule();
				
				schedule.setScheduleID( new Integer(((java.math.BigDecimal)row[0]).intValue()) );
				schedule.setScheduleName( (String)row[1] );
				schedule.setThermostatTypeID( new Integer(((java.math.BigDecimal)row[2]).intValue()) );
				schedule.setAccountID( new Integer(((java.math.BigDecimal)row[3]).intValue()) );
				schedule.setInventoryID( new Integer(((java.math.BigDecimal)row[4]).intValue()) );
				
				return schedule;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage() , e );
		}
		
		return null;
	}
	
	public static LMThermostatSchedule[] getAllThermostatSchedules(int accountID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = " + accountID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			LMThermostatSchedule[] schedules = new LMThermostatSchedule[ stmt.getRowCount() ];
			
			for (int i = 0; i < stmt.getRowCount(); i++) {
				Object[] row = stmt.getRow(i);
				schedules[i] = new LMThermostatSchedule();
				
				schedules[i].setScheduleID( new Integer(((java.math.BigDecimal)row[0]).intValue()) );
				schedules[i].setScheduleName( (String)row[1] );
				schedules[i].setThermostatTypeID( new Integer(((java.math.BigDecimal)row[2]).intValue()) );
				schedules[i].setAccountID( new Integer(((java.math.BigDecimal)row[3]).intValue()) );
				schedules[i].setInventoryID( new Integer(((java.math.BigDecimal)row[4]).intValue()) );
			}
			
			return schedules;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * @return
	 */
	public Integer getAccountID() {
		return accountID;
	}

	/**
	 * @return
	 */
	public Integer getInventoryID() {
		return inventoryID;
	}

	/**
	 * @return
	 */
	public Integer getScheduleID() {
		return scheduleID;
	}

	/**
	 * @return
	 */
	public String getScheduleName() {
		return scheduleName;
	}

	/**
	 * @return
	 */
	public Integer getThermostatTypeID() {
		return thermostatTypeID;
	}

	/**
	 * @param integer
	 */
	public void setAccountID(Integer integer) {
		accountID = integer;
	}

	/**
	 * @param integer
	 */
	public void setInventoryID(Integer integer) {
		inventoryID = integer;
	}

	/**
	 * @param integer
	 */
	public void setScheduleID(Integer integer) {
		scheduleID = integer;
	}

	/**
	 * @param string
	 */
	public void setScheduleName(String string) {
		scheduleName = string;
	}

	/**
	 * @param integer
	 */
	public void setThermostatTypeID(Integer integer) {
		thermostatTypeID = integer;
	}

}
