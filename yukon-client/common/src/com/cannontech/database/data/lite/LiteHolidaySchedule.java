package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 11:08:47 AM)
 * @author: 
 */
public class LiteHolidaySchedule extends LiteBase implements Comparable
{
	private String holidayScheduleName;
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteHolidaySchedule()
{
	super();

	setLiteType(LiteTypes.HOLIDAY_SCHEDULE);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteHolidaySchedule(int scheduleID)
{
	super();

	setLiteID( scheduleID );
	setLiteType(LiteTypes.HOLIDAY_SCHEDULE);
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:09:46 AM)
 * @return int
 * @param val java.lang.Object
 */
public int compareTo(Object val) 
{
	int thisVal = getLiteID();
	int anotherVal = ((LiteHolidaySchedule)val).getLiteID();

	return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getHolidayScheduleID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getHolidayScheduleName() {
	return holidayScheduleName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
	String sqlString = "SELECT HolidayScheduleID,HolidayScheduleName "  + 
		"FROM " + com.cannontech.database.db.holiday.HolidaySchedule.TABLE_NAME +
		" where HolidayScheduleID = " + getHolidayScheduleID();

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try 
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			setHolidayScheduleID( rset.getInt(1) );
			setHolidayScheduleName( rset.getString(2) );
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}

	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setHolidayScheduleID( int scheduleID )
{
	setLiteID( scheduleID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setHolidayScheduleName( String name )
{
	holidayScheduleName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getHolidayScheduleName();
}
}
