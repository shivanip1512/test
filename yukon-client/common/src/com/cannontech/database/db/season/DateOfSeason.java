package com.cannontech.database.db.season;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 10:32:52 AM)
 * @author: 
 */
public class DateOfSeason extends com.cannontech.database.db.DBPersistent 
{
	private Integer seasonScheduleID;
	private String seasonName;
	private Integer seasonStartMonth;
	private Integer seasonStartDay;
	private Integer seasonEndMonth;
	private Integer seasonEndDay;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"SEASONSCHEDULEID", "SEASONNAME", "SEASONSTARTMONTH", 
		"SEASONSTARTDAY", "SEASONENDMONTH", "SEASONENDDAY"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "SeasonScheduleID" };

	public static final String TABLE_NAME = "DateOfSeason";

/**
 * DateOfSeason constructor comment.
 */
public DateOfSeason() {
	super();
}
/**
 * DateOfSeason constructor comment.
 */
public DateOfSeason(Integer scheduleID, String name, Integer startMonth, Integer startDay, Integer endMonth, Integer endDay) {
	super();
	seasonScheduleID = scheduleID;
	seasonName = name;
	seasonStartMonth = startMonth;
	seasonStartDay = startDay;
	seasonEndMonth = endMonth;
	seasonEndDay = endDay;
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:34:05 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getSeasonScheduleID(), getSeasonName(),
		getSeasonStartMonth(), getSeasonStartDay(),
		getSeasonEndMonth(), getSeasonEndDay()
	};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:34:22 AM)
 */
public void delete() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllDateSeasons(Integer seasonScheduleID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt = null;

	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	try
	{
		java.sql.Statement stat = conn.createStatement();

		stat.execute("DELETE FROM DateOfSeason WHERE seasonScheduleID=" + seasonScheduleID);

		if (stat != null)
			stat.close();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:52:15 PM)
 * @param seasonScheduleID java.lang.Integer
 * @param dbAlias java.lang.String
 */
public static final java.util.Vector getAllSeasonDates(Integer scheduleID, java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector(5);
	Integer seasonScheduleID = null;
	String 	seasonName = null;
	Integer seasonStartMonth = null;
	Integer seasonStartDay = null;
	Integer seasonEndMonth = null;
	Integer seasonEndDay = null;

	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + SETTER_COLUMNS[1] +"," 
		+ SETTER_COLUMNS[2] + "," 
		+ SETTER_COLUMNS[3] + "," 
		+ SETTER_COLUMNS[4] + ","
		+ SETTER_COLUMNS[5] +
		" FROM " + TABLE_NAME +
		" WHERE " + CONSTRAINT_COLUMNS[0] + 
		"=? ORDER BY " + SETTER_COLUMNS[2];

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
				seasonScheduleID = scheduleID;
				seasonName = rset.getString(SETTER_COLUMNS[1]);
				seasonStartMonth = new Integer( rset.getInt(SETTER_COLUMNS[2]) );
				seasonStartDay = new Integer(rset.getInt(SETTER_COLUMNS[3]));
				seasonEndMonth = new Integer( rset.getInt(SETTER_COLUMNS[4]) );
				seasonEndDay = new Integer( rset.getInt(SETTER_COLUMNS[5]) );
				
				returnVector.addElement( new DateOfSeason(
						seasonScheduleID, 
						seasonName, 
						seasonStartMonth, 
						seasonStartDay, 
						seasonEndMonth,
						seasonEndDay) );				
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
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSeasonStartDay() {
	return seasonStartDay;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSeasonStartMonth() {
	return seasonStartMonth;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:40:28 PM)
 * @return java.lang.String
 */
public java.lang.String getSeasonName() {
	return seasonName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSeasonScheduleID() {
	return seasonScheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSeasonEndMonth() {
	return seasonEndMonth;
}

public java.lang.Integer getSeasonEndDay() {
	return seasonEndDay;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:33:53 AM)
 */
public void retrieve() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
/**
 * Insert the method's description here.
 * @param newSeasonStartDay java.lang.Integer
 */
public void setSeasonStartDay(java.lang.Integer newSeasonStartDay) {
	seasonStartDay = newSeasonStartDay;
}
/**
 * Insert the method's description here.
 * @param newSeasonStartMonth java.lang.Integer
 */
public void setSeasonStartMonth(java.lang.Integer newSeasonStartMonth) {
	seasonStartMonth = newSeasonStartMonth;
}
/**
 * Insert the method's description here.
 * @param newSeasonName java.lang.String
 */
public void setSeasonName(java.lang.String newSeasonName) {
	seasonName = newSeasonName;
}
/**
 * Insert the method's description here.
 * @param newSeasonScheduleID java.lang.Integer
 */
public void setSeasonScheduleID(java.lang.Integer newSeasonScheduleID) {
	seasonScheduleID = newSeasonScheduleID;
}
/**
 * Insert the method's description here.
 * @param newSeasonEndMonth java.lang.Integer
 */
public void setSeasonEndMonth(java.lang.Integer newSeasonEndMonth) {
	seasonEndMonth = newSeasonEndMonth;
}

public void setSeasonEndDay(java.lang.Integer newSeasonEndDay) {
	seasonEndDay = newSeasonEndDay;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:34:35 AM)
 */
public void update() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
}
