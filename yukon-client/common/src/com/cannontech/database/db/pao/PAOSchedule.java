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
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableMap;

/**
 * DB object for the table PAOSchedule
 */
public class PAOSchedule extends DBPersistent implements CTIDbChange, Comparable<PAOSchedule>
{
	private Integer scheduleID = null;
	//Set the time to now - 4 hours
	private Date nextRunTime = new Date( System.currentTimeMillis() - 14400000 );

	private Date lastRunTime = CtiUtilities.get1990GregCalendar().getTime();
	private Integer intervalRate = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String scheduleName = CtiUtilities.STRING_NONE;
	private boolean disabled = false;
	
	static final ImmutableMap<Integer, String> intervalStrings =
	       new ImmutableMap.Builder<Integer, String>()
	           .put(0, CtiUtilities.STRING_NONE)
	           .put(300, "5 minutes")
	           .put(420, "7 minutes")
	           .put(600, "10 minutes")
	           .put(720, "12 minutes")
	           .put(900, "15 minutes")
	           .put(1200, "20 minutes")
	           .put(1500, "25 minutes")
	           .put(1800, "30 minutes")
	           .put(3600, "1 hour")
	           .put(7200, "2 hour")
	           .put(21600, "6 hour")
	           .put(43200, "12 hour")
	           .put(86400, "1 day")
	           .put(172800, "2 days")
	           .put(432000, "5 days")
	           .put(604800, "7 days")
	           .put(1209600, "14 days")
	           .put(2592000, "30 days")
	           .build();
	
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
			setScheduleID(getNextPAOScheduleID());

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
				item.setNextRunTime( rset.getTimestamp(2) );
				item.setLastRunTime( rset.getTimestamp(3) );
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
	
	public final static Integer getNextPAOScheduleID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("PAOSchedule");
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
	public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType)
	{
		DBChangeMsg[] msgs = {
			new DBChangeMsg(
				getScheduleID().intValue(),
				DBChangeMsg.CHANGE_PAO_SCHEDULE_DB,
				DBChangeMsg.CAT_PAO_SCHEDULE,
				DBChangeMsg.CAT_PAO_SCHEDULE,
				dbChangeType)
		};

		return msgs;
	}

	/**
	 * @return
	 */
	public Integer getIntervalRate() {
		return intervalRate;
	}
	
	public String getIntervalRateString() {
	    String intervalString = intervalStrings.get(intervalRate);
	    return intervalString == null ? "?" : intervalString;
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

    @Override
    public int compareTo(PAOSchedule o) {
        return this.getScheduleName().compareToIgnoreCase(o.getScheduleName());
    }

}