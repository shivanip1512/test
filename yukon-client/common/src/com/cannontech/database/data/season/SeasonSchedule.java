package com.cannontech.database.data.season;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @author: 
 */
public class SeasonSchedule extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.season.SeasonSchedule seasonSchedule = null;

	//objects of type com.cannontech.database.db.season.DateOfSeason will only go in here
	private java.util.Vector dateOfSeasonVector = null;
/**
 * SeasonSchedule constructor comment.
 */
public SeasonSchedule() {
	super();
}
/**
 * SeasonSchedule constructor comment.
 */
public SeasonSchedule(Integer id)
{
	super();

	setScheduleID(id);
}
/**
 * SeasonSchedule constructor comment.
 */
public SeasonSchedule(Integer id, String name)
{
	super();

	setScheduleID(id);
	setScheduleName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 */
public void add() throws java.sql.SQLException 
{
	getSeasonSchedule().add();
		
	for (int i = 0; i < getSeasonDatesVector().size(); i++)
		((com.cannontech.database.db.season.DateOfSeason) getSeasonDatesVector().elementAt(i)).add();

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:36:20 AM)
 */
public void delete() throws java.sql.SQLException 
{
	com.cannontech.database.db.season.DateOfSeason.deleteAllDateSeasons(getSeasonSchedule().getScheduleID(), getDbConnection());

	getSeasonSchedule().delete();	
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getScheduleID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_SEASON_SCHEDULE_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_SEASON_SCHEDULE,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_SEASON_SCHEDULE,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getSeasonDatesVector()
{
	if (dateOfSeasonVector == null)
		dateOfSeasonVector = new java.util.Vector();

	return dateOfSeasonVector;
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.season.SeasonSchedule
 */
private com.cannontech.database.db.season.SeasonSchedule getSeasonSchedule()
{
	if (seasonSchedule == null)
		seasonSchedule = new com.cannontech.database.db.season.SeasonSchedule();

	return seasonSchedule;
}
/**
 * Insert the method's description here.
 * @return Integer
 */
public Integer getScheduleID()
{
	return getSeasonSchedule().getScheduleID();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:21:32 PM)
 * @return String
 */
public String getScheduleName()
{
	return getSeasonSchedule().getScheduleName();
}
/**
 * Insert the method's description here.
 */
public void retrieve() throws java.sql.SQLException 
{
	getSeasonSchedule().retrieve();

	java.util.Vector seasonDates = com.cannontech.database.db.season.DateOfSeason.getAllSeasonDates(
				getSeasonSchedule().getScheduleID(), getDbConnection() );

	for( int i = 0; i < seasonDates.size(); i++ )
		getSeasonDatesVector().add( seasonDates.get(i) );


}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getSeasonSchedule().setDbConnection(conn);
	
	for( int i = 0; i < getSeasonDatesVector().size(); i++ )
		((com.cannontech.database.db.season.DateOfSeason)getSeasonDatesVector().get(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @return com.cannontech.database.data.season.SeasonSchedule
 */
public void setScheduleID( Integer newID )
{
	getSeasonSchedule().setScheduleID( newID );
	
	for( int i = 0; i < getSeasonDatesVector().size(); i++ )
		((com.cannontech.database.db.season.DateOfSeason)getSeasonDatesVector().get(i)).setSeasonScheduleID(newID);
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @return com.cannontech.database.data.season.SeasonSchedule
 */
public void setScheduleName( String newName )
{
	getSeasonSchedule().setScheduleName( newName );	
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 */
public String toString()
{
	return getScheduleName();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 10:35:21 AM))
 */
public void update() throws java.sql.SQLException
{
	getSeasonSchedule().update();

	//delete all the Dates
	com.cannontech.database.db.season.DateOfSeason.deleteAllDateSeasons(getSeasonSchedule().getScheduleID(), getDbConnection());

	for (int i = 0; i < getSeasonDatesVector().size(); i++)
		((com.cannontech.database.db.season.DateOfSeason) getSeasonDatesVector().elementAt(i)).add();	
}
}
