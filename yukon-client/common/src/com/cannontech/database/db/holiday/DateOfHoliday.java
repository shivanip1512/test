package com.cannontech.database.db.holiday;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:32:52 AM)
 * @author: 
 */
public class DateOfHoliday extends com.cannontech.database.db.DBPersistent 
{
	private Integer holidayScheduleID;
	private String holidayName;
	private Integer holidayMonth;
	private Integer holidayDay;
	private Integer holidayYear;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"HOLIDAYSCHEDULEID", "HOLIDAYNAME", "HOLIDAYMONTH", 
		"HOLIDAYDAY", "HOLIDAYYEAR"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "HolidayScheduleID" };

	public static final String TABLE_NAME = "DateOfHoliday";

/**
 * DateOfHoliday constructor comment.
 */
public DateOfHoliday() {
	super();
}
/**
 * DateOfHoliday constructor comment.
 */
public DateOfHoliday(Integer scheduleID, String name, Integer month, Integer day, Integer year) {
	super();
	holidayScheduleID = scheduleID;
	holidayName = name;
	holidayMonth = month;
	holidayDay = day;
	holidayYear = year;
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:34:05 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getHolidayScheduleID(), getHolidayName(),
		getHolidayMonth(), getHolidayDay(),
		getHolidayYear()
	};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:34:22 AM)
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
public static boolean deleteAllDateHolidays(Integer holidayScheduleID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt = null;

	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	java.sql.Statement stat = null;
	try
	{
		stat = conn.createStatement();

		stat.execute("DELETE FROM DateOfHoliday WHERE holidayScheduleID=" + holidayScheduleID);

	}
	catch (Exception e)
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
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:52:15 PM)
 * @param holidayScheduleID java.lang.Integer
 * @param dbAlias java.lang.String
 */
public static final java.util.Vector getAllHolidayDates(Integer scheduleID, java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector(5);
	Integer holidayScheduleID = null;
	String 	holidayName = null;
	Integer holidayMonth = null;
	Integer holidayDay = null;
	Integer holidayYear = null;

	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + SETTER_COLUMNS[1] +"," 
		+ SETTER_COLUMNS[2] + "," 
		+ SETTER_COLUMNS[3] + "," 
		+ SETTER_COLUMNS[4] +
		" FROM " + TABLE_NAME +
		" WHERE " + CONSTRAINT_COLUMNS[0] + 
		"=? ORDER BY " + SETTER_COLUMNS[0];

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
				holidayScheduleID = scheduleID;
				holidayName = rset.getString(SETTER_COLUMNS[1]);
				holidayMonth = new Integer( rset.getInt(SETTER_COLUMNS[2]) );
				holidayDay = new Integer(rset.getInt(SETTER_COLUMNS[3]));
				holidayYear = new Integer( rset.getInt(SETTER_COLUMNS[4]) );
				
				returnVector.addElement( new DateOfHoliday(
						holidayScheduleID, 
						holidayName, 
						holidayMonth, 
						holidayDay, 
						holidayYear) );				
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}


	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHolidayDay() {
	return holidayDay;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHolidayMonth() {
	return holidayMonth;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @return java.lang.String
 */
public java.lang.String getHolidayName() {
	return holidayName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHolidayScheduleID() {
	return holidayScheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHolidayYear() {
	return holidayYear;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:33:53 AM)
 */
public void retrieve() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @param newHolidayDay java.lang.Integer
 */
public void setHolidayDay(java.lang.Integer newHolidayDay) {
	holidayDay = newHolidayDay;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @param newHolidayMonth java.lang.Integer
 */
public void setHolidayMonth(java.lang.Integer newHolidayMonth) {
	holidayMonth = newHolidayMonth;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @param newHolidayName java.lang.String
 */
public void setHolidayName(java.lang.String newHolidayName) {
	holidayName = newHolidayName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @param newHolidayScheduleID java.lang.Integer
 */
public void setHolidayScheduleID(java.lang.Integer newHolidayScheduleID) {
	holidayScheduleID = newHolidayScheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:40:28 PM)
 * @param newHolidayYear java.lang.Integer
 */
public void setHolidayYear(java.lang.Integer newHolidayYear) {
	holidayYear = newHolidayYear;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:34:35 AM)
 */
public void update() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
}
