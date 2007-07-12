/*
 * Created on Nov 19, 2003
 */
package com.cannontech.cttp.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

/**
 * @author aaron
 */
public class CttpCmd extends DBPersistent {

	public static final String TABLE_NAME = "CTTPCMD";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"UserID", "TimeSent", "LastUpdated", "Status", "ClearCmd", "DegOffset", "Duration"
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "TrackingID" };
		
	private Integer trackingID;
	private Integer userID;
	private Date timeSent = new Date();
	private Date lastUpdated = new Date();
	private String status = "PENDING";
	private Character clearCmd = new Character('N');
	private Integer degOffset;
	private Integer duration;
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] values = {
			getTrackingID(), getUserID(), getTimeSent(), getLastUpdated(), getStatus(), getClearCmd(), getDegOffset(), getDuration()
		};
		add(TABLE_NAME, values);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getTrackingID());

	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getTrackingID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setUserID((Integer) results[0]);			
			setTimeSent((Date) results[1]);
			setLastUpdated((Date) results[2]);
			setStatus((String) results[3]);
			setClearCmd(new Character(results[4].toString().charAt(0)));
			setDegOffset((Integer) results[5]);
			setDuration((Integer) results[6]);
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = 
		{
			getUserID(),getTimeSent(), getLastUpdated(), getStatus(), getClearCmd(),getDegOffset() ,getDuration()
		};
		
		Object[] constraintValues = { getTrackingID() };
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * @return
	 */
	public Character getClearCmd() {
		return clearCmd;
	}

	/**
	 * @return
	 */
	public Integer getDegOffset() {
		return degOffset;
	}

	/**
	 * @return
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * @return
	 */
	public Integer getTrackingID() {
		return trackingID;
	}

	/**
	 * @return
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * @param character
	 */
	public void setClearCmd(Character character) {
		clearCmd = character;
	}

	/**
	 * @param integer
	 */
	public void setDegOffset(Integer integer) {
		degOffset = integer;
	}

	/**
	 * @param integer
	 */
	public void setDuration(Integer integer) {
		duration = integer;
	}

	/**
	 * @param integer
	 */
	public void setTrackingID(Integer integer) {
		trackingID = integer;
	}

	/**
	 * @param integer
	 */
	public void setUserID(Integer integer) {
		userID = integer;
	}
	
	/**
	 * @return
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return
	 */
	public Date getTimeSent() {
		return timeSent;
	}

	/**
	 * @param date
	 */
	public void setLastUpdated(Date date) {
		lastUpdated = date;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		status = string;
	}

	/**
	 * @param date
	 */
	public void setTimeSent(Date date) {
		timeSent = date;
	}
	

	public static final synchronized int getNextID(Connection conn) {
		int retVal = 0;
		Statement stmt = null;
		ResultSet rset = null;

		try	{
			if(conn == null ) {
						throw new IllegalStateException("Database connection cannot be (null).");
			}
			else {
				stmt = conn.createStatement();
				rset = stmt.executeQuery("select max(TrackingID) from CTTPCMD");

				// Just one please
				if( rset.next() )
					retVal = rset.getInt(1) + 1;
			}
		}
		catch( java.sql.SQLException e ) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally {
			SqlUtils.close(rset, stmt);
		}

		return retVal;
	}

}
