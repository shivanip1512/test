/*
 * Created on Dec 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite;

import com.cannontech.database.db.tou.TOUDay;
import com.cannontech.database.db.tou.TOUDayMapping;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteTOUDay extends LiteBase
{
	private String dayName;
	private int dayOffset;
	private int scheduleID;

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUDay()
{
	super();

	setLiteType(LiteTypes.TOU_DAY);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUDay(int dayID)
{
	super();

	setLiteID( dayID );
	setLiteType(LiteTypes.TOU_DAY);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUDay(int dayID, String dayName_, int dayOffset_, int schedID_ )
{
	this( dayID );
	setDayName( dayName_ );
	setDayOffset( dayOffset_ );
	setScheduleID( schedID_ );
}

public int getDayID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/2004 11:14:47 AM)
 * @return java.lang.String
 */
public String getDayName() {
	return dayName;
}

public int getDayOffset() {
	return dayOffset;
}

public int getScheduleID() {
	return scheduleID;
}

/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 
   com.cannontech.database.SqlStatement s = 
	  new com.cannontech.database.SqlStatement(
		"SELECT d.TOUDayID, d.TOUDayName, dm.TOUDayOffset, dm.TOUScheduleID FROM " + 
		com.cannontech.database.db.tou.TOUDay.TABLE_NAME + " d" +
		com.cannontech.database.db.tou.TOUDayMapping.TABLE_NAME + " dm WHERE " +
		"d.TOUDayID = " + getDayID() + " AND dm.TOUDayID = " + getDayID(), 
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find TOU Day with ID = " + getLiteID() );


	  setDayID( new Integer(s.getRow(0)[0].toString()).intValue() );
	  setDayName( s.getRow(0)[1].toString() );
	  setDayOffset( new Integer(s.getRow(0)[2].toString()).intValue() );
	  setScheduleID( new Integer(s.getRow(0)[3].toString()).intValue() );
   }
   catch( Exception e )
   {
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
   }
      
}
/**
 * Insert the method's description here.
 * Creation date:(12/17/2004 11:13:50 AM)
 * @return void
 */
public void setDayID( int dayID )
{
	setLiteID( dayID );
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/2004 11:13:50 AM)
 * @return void
 */
public void setDayName( String name )
{
	dayName = name;
}

public void setDayOffset( int offset )
{
	dayOffset = offset;
}

public void setScheduleID( int schedID )
{
	scheduleID = schedID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/17/2004 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getDayName();
}

}