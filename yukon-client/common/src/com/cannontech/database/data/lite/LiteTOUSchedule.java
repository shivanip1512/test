/*
 * Created on Sep 22, 2004
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
public class LiteTOUSchedule extends LiteBase
{
	private String scheduleName;

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUSchedule()
{
	super();

	setLiteType(LiteTypes.TOU_SCHEDULE);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUSchedule(int schedID)
{
	super();

	setLiteID( schedID );
	setLiteType(LiteTypes.TOU_SCHEDULE);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteTOUSchedule(int schedID, String schedName_ )
{
	this( schedID );
	setScheduleName( schedName_ );
}

public int getScheduleID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 11:14:47 AM)
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
		 "SELECT TOUScheduleID, TOUScheduleName "  + 
			"FROM " + com.cannontech.database.db.tou.TOUSchedule.TABLE_NAME +
			" where TOUScheduleID = " + getScheduleID(),
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find TOU schedule with ID = " + getLiteID() );


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
 * Creation date:(9/24/2004 11:13:50 AM)
 * @return void
 */
public void setScheduleID( int schedID )
{
	setLiteID( schedID );
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 11:13:50 AM)
 * @return void
 */
public void setScheduleName( String name )
{
	scheduleName = name;
}

/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getScheduleName();
}


}