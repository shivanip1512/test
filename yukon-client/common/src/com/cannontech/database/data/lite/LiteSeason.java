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
public class LiteSeason extends LiteBase
{
	private String seasonName;
/**
 * LiteSeason constructor comment.
 */
public LiteSeason()
{
	super();

	setLiteType(LiteTypes.SEASON);
}
/**
 * LiteSeason constructor comment.
 */
public LiteSeason(int seasID)
{
	super();

	setLiteID( seasID );
	setLiteType(LiteTypes.SEASON);
}

/**
 * LiteSeason constructor comment.
 */
public LiteSeason(int seasID, String schdName_ )
{
	this( seasID );
	setSeasonName( schdName_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return int
 */
public int getSeasonID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:14:47 AM)
 * @return java.lang.String
 */
public String getSeasonName() {
	return seasonName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
   com.cannontech.database.SqlStatement s = 
	  new com.cannontech.database.SqlStatement(
		 "SELECT SeasonID,SeasonName "  + 
			"FROM " + com.cannontech.database.db.season.SeasonSchedule.TABLE_NAME +
			" where SeasonID = " + getSeasonID(),
		 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
	  s.execute();

	  if( s.getRowCount() <= 0 )
		 throw new IllegalStateException("Unable to find Season with seasonID = " + getLiteID() );


	  setSeasonID( new Integer(s.getRow(0)[0].toString()).intValue() );
	  setSeasonName( s.getRow(0)[1].toString() );
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
public void setSeasonID( int seasID )
{
	setLiteID( seasID );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:13:50 AM)
 * @return void
 */
public void setSeasonName( String name )
{
	seasonName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:15:17 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getSeasonName();
}
}