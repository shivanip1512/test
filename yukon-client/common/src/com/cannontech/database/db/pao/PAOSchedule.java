package com.cannontech.database.db.pao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * DB object for the table PAOSchedule
 */
public class PAOSchedule extends DBPersistent implements CTIDbChange
{
	private Integer scheduleID = null;
	//Set the time to now - 4 hours
	private Date nextRunTime =
        new Date( System.currentTimeMillis() - 14400000 );

	private Date lastRunTime = CtiUtilities.get1990GregCalendar().getTime();
	private Integer intervalRate = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String scheduleName = CtiUtilities.STRING_NONE;
	private boolean disabled = false;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"NextRunTime", "LastRunTime",
		"IntervalRate", "ScheduleName",
		"Disabled"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

	public static final String TABLE_NAME = "PAOSchedule";


	private static final String ALL_SCHEDULES_SQL = 
			"select ScheduleID, NextRunTime, LastRunTime, IntervalRate, " +
			"ScheduleName, Disabled FROM " + TABLE_NAME +
			" order by ScheduleName"; 

	/**
	 * default constructor.
	 */
	public PAOSchedule() {
		super();
	}

	/**
	 * default constructor.
	 */
	public PAOSchedule( Integer schedID ) {
		this();
		setScheduleID( schedID );
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		if( getScheduleID() == null )
			setScheduleID( getNextPAOScheduleID(getDbConnection()) );

		Object addValues[] = {
			getScheduleID(), getNextRunTime(),
			getLastRunTime(), getIntervalRate(), getScheduleName(),
			(isDisabled() ? CtiUtilities.trueChar : CtiUtilities.falseChar)
		};
	
		add( TABLE_NAME, addValues );
	}
	
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Object values[] = { getScheduleID() };
	
		//delete any potential foreign keys to this object
		delete( "PAOScheduleAssignment", "ScheduleID", getScheduleID() );
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	

	/**
	 * This method was created in VisualAge.
	 */
	public static final PAOSchedule[] getAllPAOSchedules()
	{
		Vector tmpList = new Vector();
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		java.sql.Connection conn = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );

			pstmt = conn.prepareStatement( ALL_SCHEDULES_SQL );				
			rset = pstmt.executeQuery();							
	
			while( rset.next() ) {
				PAOSchedule item = new PAOSchedule();

				item.setScheduleID( new Integer(rset.getInt(1)) );
				item.setNextRunTime( rset.getDate(2) );
				item.setLastRunTime( rset.getDate(3) );
				item.setIntervalRate( new Integer(rset.getInt(4)) );
				item.setScheduleName( new String(rset.getString(5)) );
				item.setDisabled(
					CtiUtilities.trueChar.charValue() == rset.getString(6).charAt(0) );			

				tmpList.add( item );
			}
						
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			SqlUtils.close(rset, pstmt, conn );
		}


		PAOSchedule retVal[] = new PAOSchedule[ tmpList.size() ];
		tmpList.toArray( retVal );
		return retVal;
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param deviceID java.lang.Integer
	 */
	public static synchronized boolean deleteAllPAOSchedules(int paoID, java.sql.Connection conn )
	{
		java.sql.Statement stat = null;
		try
		{
			if( conn == null )
				throw new IllegalStateException("Database connection should not be null.");
	
			stat = conn.createStatement();
			
			stat.execute( 
					"DELETE FROM " + TABLE_NAME + 
					" WHERE PAOid=" + paoID );
	
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}
		finally{
			SqlUtils.close(stat);
		}
	
	
		return true;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public final static Integer getNextPAOScheduleID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
		
			
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
			
		try {
			 stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(ScheduleID)+1 FROM " + TABLE_NAME );	
					
			 //get the first returned result
			 rset.next();
			 return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) {
			 e.printStackTrace();
		}
		finally 
		{
			SqlUtils.close(rset, stmt );
		}
			
		//strange, should not get here
		return new Integer(CtiUtilities.NONE_ZERO_ID);
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getScheduleID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setNextRunTime( new Date( ((Timestamp)results[0]).getTime() ) );		
			setLastRunTime( new Date( ((Timestamp)results[1]).getTime() ) );			
			setIntervalRate( (Integer) results[2] );
			setScheduleName( (String)results[3] );
			
			setDisabled(
				CtiUtilities.trueChar.charValue() == results[4].toString().charAt(0) );			
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	
	
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] = {
			getNextRunTime(), getLastRunTime(),
			getIntervalRate(), getScheduleName(),
			(isDisabled() ? CtiUtilities.trueChar : CtiUtilities.falseChar)			
		};
						
		Object constraintValues[] = { getScheduleID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}


	/**
	 * Generates a DB Change message for this db object
	 */
	public DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		DBChangeMsg[] msgs = {
			new DBChangeMsg(
				getScheduleID().intValue(),
				DBChangeMsg.CHANGE_PAO_SCHEDULE_DB,
				DBChangeMsg.CAT_PAO_SCHEDULE,
				DBChangeMsg.CAT_PAO_SCHEDULE,
				typeOfChange)
		};


		return msgs;
	}

	/*
	public final static boolean isMasterProgram(Integer anID, String databaseAlias) throws java.sql.SQLException 
	{
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
			"SELECT ExclusionID from " + TABLE_NAME + " WHERE PaoID = " + anID, databaseAlias);
			
		try
		{
			stmt.execute();
			return (stmt.getRowCount() > 0 );
		}
		catch( Exception e )
		{
			return false;
		}
	
	}
	*/
	

	/**
	 * @return
	 */
	public Integer getIntervalRate() {
		return intervalRate;
	}

	/**
	 * @return
	 */
	public Date getLastRunTime() {
		return lastRunTime;
	}

	/**
	 * @return
	 */
	public Date getNextRunTime() {
		return nextRunTime;
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
	 * @param integer
	 */
	public void setIntervalRate(Integer integer) {
		intervalRate = integer;
	}

	/**
	 * @param date
	 */
	public void setLastRunTime(Date date) {
		lastRunTime = date;
	}

	/**
	 * @param date
	 */
	public void setNextRunTime(Date date) {
		nextRunTime = date;
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
	 * @return
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param b
	 */
	public void setDisabled(boolean b) {
		disabled = b;
	}

}