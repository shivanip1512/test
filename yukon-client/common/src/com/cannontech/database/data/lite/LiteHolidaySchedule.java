package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 11:08:47 AM)
 * @author: 
 */
public class LiteHolidaySchedule extends LiteBase
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
 * LiteHolidaySchedule constructor comment.
 */
public LiteHolidaySchedule(int scheduleID, String schdName_ )
{
	this( scheduleID );
	setHolidayScheduleName( schdName_ );
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
   com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT HolidayScheduleID,HolidayScheduleName "  + 
            "FROM " + com.cannontech.database.db.holiday.HolidaySchedule.TABLE_NAME +
            " where HolidayScheduleID = " + getHolidayScheduleID(),
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );


      setHolidayScheduleID( new Integer(s.getRow(0)[0].toString()).intValue() );
      setHolidayScheduleName( s.getRow(0)[1].toString() );
   }
   catch( Exception e )
   {
      com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
