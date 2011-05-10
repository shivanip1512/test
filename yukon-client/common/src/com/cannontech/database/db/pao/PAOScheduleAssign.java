package com.cannontech.database.db.pao;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

/**
 * DB object for the table PAOSchedule
 */
public class PAOScheduleAssign extends DBPersistent
{
	private Integer eventID = null;
	private Integer scheduleID = new Integer(INVALID_SCHEDULEID);
	private Integer paoID = null;
	private String command = CtiUtilities.STRING_NONE;
	private String disableOvUv = "N";

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ScheduleID", "PaoID", "Command", "DisableOvUv"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

	public static final String TABLE_NAME = "PAOScheduleAssignment";

	//how many schedules we allow per PAO
	public static final int MAX_SHEDULES_PER_PAO = 10;

	//represents a invalid PAOScheduleAssign instance
	public static final int INVALID_SCHEDULEID= -1;


	private static final String ALL_SCHEDULES_SQL = 
			"select EventID, ScheduleID, PaoID, Command, DisableOvUv " +
			"FROM " + TABLE_NAME +
			" where paoID = ? " +
			"order by paoID"; 

	/**
	 * default constructor.
	 */
	public PAOScheduleAssign() {
		super();
	}

	/**
	 * default constructor.
	 */
	public PAOScheduleAssign( Integer evID ) {
		this();
		setEventID( evID );
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		if( getScheduleID().intValue() == INVALID_SCHEDULEID)
			return;

		if( getEventID() == null )
			setEventID( getNextEventID() );

		Object addValues[] = {
			getEventID(), getScheduleID(), getPaoID(),
			getCommand(), getDisableOvUv()
		};
	
		add( TABLE_NAME, addValues );
	}
	
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Object values[] = { getEventID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	

	/**
	 * This method was created in VisualAge.
	 */
	public static final PAOScheduleAssign[] getAllPAOSchedAssignments( int parentPaoID, java.sql.Connection conn )
	{
		Vector tmpList = new Vector();
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			if( conn == null )
			{
				throw new IllegalStateException("Database connection should not be null.");
			}
			else
			{
				pstmt = conn.prepareStatement( ALL_SCHEDULES_SQL );
				pstmt.setInt( 1, parentPaoID );
	
				rset = pstmt.executeQuery();					
		
				while( rset.next() ) {
					PAOScheduleAssign item = new PAOScheduleAssign();
	
					item.setEventID( new Integer(rset.getInt(1)) );
					item.setScheduleID( new Integer(rset.getInt(2)) );
					item.setPaoID( new Integer(rset.getInt(3)) );
					item.setCommand( new String(rset.getString(4)) );
					item.setDisableOvUv( new String(rset.getString(5)) );
	
					tmpList.add( item );
				}
			}
						
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if( pstmt != null ) pstmt.close();
				if( rset != null ) rset.close();
			} 
			catch( java.sql.SQLException e2 ) {
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}


		PAOScheduleAssign retVal[] = new PAOScheduleAssign[ tmpList.size() ];
		tmpList.toArray( retVal );
		return retVal;
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param deviceID java.lang.Integer
	 */
	public static synchronized boolean deleteAllPAOScheduleAssignments(int paoID, java.sql.Connection conn )
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
	public int getNextEventID()
	{
		NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();

		return nextValueHelper.getNextValue("PAOScheduleAssignment");
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getEventID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{			
			setScheduleID( (Integer) results[0] );
			setPaoID( (Integer)results[1] );
			setCommand( (String)results[2] );
			setDisableOvUv( (String)results[3] );
		}

	}
	
	
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		if( getScheduleID().intValue() == INVALID_SCHEDULEID )
			return;

		Object setValues[] = {
			getScheduleID(), getPaoID(), getCommand(), getDisableOvUv()
		};
						
		Object constraintValues[] = { getEventID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getScheduleID() {
		return scheduleID;
	}


	/**
	 * @param integer
	 */
	public void setScheduleID(Integer integer) {
		scheduleID = integer;
	}

	/**
	 * @return
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return
	 */
	public Integer getEventID() {
		return eventID;
	}

	/**
	 * @return
	 */
	public Integer getPaoID() {
		return paoID;
	}

	/**
	 * @param string
	 */
	public void setCommand(String string) {
		command = string;
	}

	/**
	 * @param integer
	 */
	public void setEventID(Integer integer) {
		eventID = integer;
	}

	/**
	 * @param integer
	 */
	public void setPaoID(Integer integer) {
		paoID = integer;
	}
	
	public void setDisableOvUv(String disableOvUv) {
	    this.disableOvUv = disableOvUv;
	}

	public String getDisableOvUv() {
	    return disableOvUv;
	}
	
	public void setDisableOvUvBoolean(boolean disableOvUv) {
	    if(disableOvUv) {
	        this.disableOvUv = "Y";
	    } else {
	        this.disableOvUv = "N";
	    }
	}
	
	public boolean getDisableOvUvBoolean() {
	    return disableOvUv.equalsIgnoreCase("N") ? false : true;
	}

}