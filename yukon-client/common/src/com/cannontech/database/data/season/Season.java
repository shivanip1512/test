/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.season;

import com.cannontech.database.db.season.SeasonSchedule;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Season extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private SeasonSchedule season = null;

/**
 * Season constructor comment.
 */
public Season() {
	super();
}
/**
 * Season constructor comment.
 */
public Season(Integer id)
{
	super();

	setSeasonID(id);
}
/**
 * Season constructor comment.
 */
public Season(Integer id, String name)
{
	super();

	setSeasonID(id);
	setSeasonName(name);
}
/**
 * Insert the method's description here.
 */
public void add() throws java.sql.SQLException 
{
	if(getSeasonID() == null)
			setSeasonID(SeasonSchedule.getNextSeasonID());
	
	getSeason().add();
		
}
/**
 * Insert the method's description here.
 */
public void delete() throws java.sql.SQLException 
{
	getSeason().delete();	
}
/**
 * Insert the method's description here.
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getSeasonID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_SEASON_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_SEASON,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_SEASON,
					typeOfChange)
	};


	return msgs;
}

private com.cannontech.database.db.season.SeasonSchedule getSeason()
{
	if (season == null)
		season = new com.cannontech.database.db.season.SeasonSchedule();

	return season;
}
/**
 * Insert the method's description here.
 * @return Integer
 */
public Integer getSeasonID()
{
	return getSeason().getSeasonID();
}
/**
 * Insert the method's description here.
 * @return String
 */
public String getSeasonName()
{
	return getSeason().getSeasonName();
}
/**
 * Insert the method's description here.
 */
public void retrieve() throws java.sql.SQLException 
{
	getSeason().retrieve();
}
/**
 * Insert the method's description here.
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getSeason().setDbConnection(conn);
	
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.season.Season
 */
public void setSeasonID( Integer newID )
{
	getSeason().setSeasonID( newID );
	
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.season.Season
 */
public void setSeasonName( String newName )
{
	getSeason().setSeasonName( newName );	
}
/**
 * Insert the method's description here.
 */
public String toString()
{
	return getSeasonName();
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:36:31 AM)
 */
public void update() throws java.sql.SQLException
{
	getSeason().update();
}
}