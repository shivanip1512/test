/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tou;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDayMapping extends com.cannontech.database.db.DBPersistent 
{
	
	private Integer scheduleID;
	private Integer dayID;
	private Integer dayOffset;

	public static final String SETTER_COLUMNS[] = { "TOUDayID", "TOUDayOffset" };

	public static final String CONSTRAINT_COLUMNS[] = { "TOUScheduleID" };
	
	public final Object SET_VALUES[] = { getDayID(), getDayOffset() };

	public final Object CONSTRAINT_VALUES[] = { getScheduleID()};

	public static final String TABLE_NAME = "TOUDayMapping";

/**
 * TOUDeviceMapping constructor comment.
 */
public TOUDayMapping() {
	super();
}

public TOUDayMapping(Integer newSchedID, Integer newDayID, Integer newDayOffset)
{
	super();
	
	scheduleID = newSchedID;
	dayID = newDayID;
	dayOffset = newDayOffset;
}

/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getScheduleID(), getDayID(), getDayOffset() };
	deleteAMapping(getDayID(), getDbConnection());
	add( TABLE_NAME, addValues );
}

/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {
	
	String values[] = { getScheduleID().toString(), getDayID().toString(), getDayOffset().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAMapping(Integer scheduleID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE TOUScheduleID=" + scheduleID;

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null)");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) 
				pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return true;
}

public void deleteAMapping(Integer scheduleID)
{
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		deleteAMapping(scheduleID, conn);

		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final java.util.Vector getAllDayIDList(Integer scheduleID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.Vector tmpList = new java.util.Vector();
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT " + SETTER_COLUMNS[0] + " FROM " + TABLE_NAME + " WHERE " + CONSTRAINT_COLUMNS[0] + "= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, scheduleID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new Integer(rset.getInt("deviceID")) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return tmpList;
}

public static final java.util.Vector getAllScheduleDays(Integer scheduleID, java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector(5);
	Integer touScheduleID = null;
	Integer touDayOffset = null;
	Integer touDayID = null;
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + SETTER_COLUMNS[0] +"," 
		+ SETTER_COLUMNS[1] + 
		" FROM " + TABLE_NAME +
		" WHERE " + CONSTRAINT_COLUMNS[0] + 
		"=? ORDER BY " + SETTER_COLUMNS[1];

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, scheduleID.intValue() );
			
			rset = pstmt.executeQuery();
	
			while( rset.next() )
			{
				touScheduleID = scheduleID;
				touDayID = new Integer( rset.getInt(SETTER_COLUMNS[1]) );
				touDayOffset = new Integer( rset.getInt(SETTER_COLUMNS[2]) );
								
				returnVector.addElement( new TOUDayMapping(
						touScheduleID, 
						touDayID, 
						touDayOffset) );				
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return returnVector;
}

public Integer getDayID() 
{
	return dayID;
}

public Integer getScheduleID() 
{
	return scheduleID;
}

public Integer getDayOffset() 
{
	return dayOffset;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, CONSTRAINT_VALUES );
	
	if (results.length == SETTER_COLUMNS.length)
	{
		setDayID((Integer) results[0]);
		setDayOffset((Integer) results[1]);		
	}
}

public void setDayID(Integer newDayID) 
{
	dayID = newDayID;
}

public void setScheduleID(Integer newScheduleID) 
{
	scheduleID = newScheduleID;
}

public void setDayOffset(Integer newOffset) 
{
	dayOffset = newOffset;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	update( TABLE_NAME, SETTER_COLUMNS, SET_VALUES, CONSTRAINT_COLUMNS, CONSTRAINT_VALUES );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final Integer getTheScheduleID(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	Integer returnedID = new Integer(0);

	String sql = "SELECT TOUScheduleID FROM " + TABLE_NAME + " WHERE deviceID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, deviceID.intValue() );
			
			rset = pstmt.executeQuery();							
			rset.next();
			returnedID = new Integer(rset.getInt(1));
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( rset != null ) rset.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return returnedID;
}

/*
public final static boolean hasSchedule(Integer deviceID) throws java.sql.SQLException 
{	
	return hasSchedule(deviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}

public final static boolean hasSchedule(Integer deviceID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT TOUScheduleID FROM " + TABLE_NAME + " WHERE DeviceID=" + deviceID,
													databaseAlias );
	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}*/
}