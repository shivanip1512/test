package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 11:08:47 AM)
 * @author: 
 */
public class LiteBaseline extends LiteBase
{
	private String baselineName;
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline()
{
	super();

	setLiteType(LiteTypes.BASELINE);
}
/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline(int baselineID)
{
	super();

	setLiteID( baselineID );
	setLiteType(LiteTypes.BASELINE);
}

/**
 * LiteHolidaySchedule constructor comment.
 */
public LiteBaseline(int baselineID, String blName_ )
{
	this( baselineID );
	setBaselineName( blName_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getBaselineID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getBaselineName() {
	return baselineName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 
   com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT baselineID, baselineName "  + 
            "FROM " + com.cannontech.database.db.baseline.Baseline.TABLE_NAME +
            " where baselineID = " + getBaselineID(),
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find baseline with ID = " + getLiteID() );


      setBaselineID( new Integer(s.getRow(0)[0].toString()).intValue() );
      setBaselineName( s.getRow(0)[1].toString() );
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
public void setBaselineID( int baselineID )
{
	setLiteID( baselineID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setBaselineName( String name )
{
	baselineName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getBaselineName();
}
}
