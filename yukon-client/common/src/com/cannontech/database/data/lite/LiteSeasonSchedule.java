/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteSeasonSchedule extends LiteBase
{
	private String scheduleName;
/**
 * LiteSeason constructor comment.
 */
public LiteSeasonSchedule()
{
	super();

	setLiteType(LiteTypes.SEASON_SCHEDULE);
}
/**
 * LiteSeason constructor comment.
 */
public LiteSeasonSchedule(int seasID)
{
	super();

	setLiteID( seasID );
	setLiteType(LiteTypes.SEASON_SCHEDULE);
}

/**
 * LiteSeason constructor comment.
 */
public LiteSeasonSchedule(int seasID, String schdName_ )
{
	this( seasID );
	setScheduleName( schdName_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getScheduleID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getScheduleName() {
	return scheduleName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
   com.cannontech.database.SqlStatement s = 
	  new com.cannontech.database.SqlStatement(
		 "SELECT ScheduleID, ScheduleName "  + 
			"FROM " + com.cannontech.database.db.season.SeasonSchedule.TABLE_NAME +
			" where ScheduleID = " + getScheduleID(),
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find Season with ScheduleID = " + getLiteID() );


	  setScheduleID( new Integer(s.getRow(0)[0].toString()).intValue() );
	  setScheduleName( s.getRow(0)[1].toString() );
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
public void setScheduleID( int seasID )
{
	setLiteID( seasID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setScheduleName( String name )
{
	scheduleName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getScheduleName();
}
}