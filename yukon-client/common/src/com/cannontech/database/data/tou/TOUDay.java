/*
 * Created on Dec 06, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.tou;

import com.cannontech.database.db.tou.TOUDayRateSwitches;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDay extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	com.cannontech.database.db.tou.TOUDay touDay = null;

	//only objects of type com.cannontech.database.db.tou.TOUDayRateSwitches will go in here
	private java.util.Vector touRateSwitchVector = null;
/**
 * TOUDay constructor comment.
 */
public TOUDay() {
	super();
}
/**
 * TOUDay constructor comment.
 */
public TOUDay(Integer id)
{
	super();

	setNameID(id);
}
/**
 * TOUDay constructor comment.
 */
public TOUDay(Integer id, String name)
{
	super();

	setNameID(id);
	setNameName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 */
public void add() throws java.sql.SQLException 
{
	getTOUDay().add();
		
	for (int i = 0; i < getTOURateSwitchVector().size(); i++)
	{
		((TOUDayRateSwitches) getTOURateSwitchVector().elementAt(i)).setDayID(getTOUDay().getDayID());
		((TOUDayRateSwitches) getTOURateSwitchVector().elementAt(i)).add();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:36:20 AM)
 */
public void delete() throws java.sql.SQLException 
{
	TOUDayRateSwitches.deleteAllDayRateSwitches(getTOUDay().getDayID(), getDbConnection());

	getTOUDay().delete();	
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getDayID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TOU_SCHEDULE_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TOU_SCHEDULE,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TOU_SCHEDULE,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getTOURateSwitchVector()
{
	if (touRateSwitchVector == null)
		touRateSwitchVector = new java.util.Vector();

	return touRateSwitchVector;
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.tou.TOUDay
 */
private com.cannontech.database.db.tou.TOUDay getTOUDay()
{
	if (touDay == null)
		touDay = new com.cannontech.database.db.tou.TOUDay();

	return touDay;
}
/**
 * Insert the method's description here.
 * @return Integer
 */
public Integer getDayID()
{
	return getTOUDay().getDayID();
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 12:21:32 PM)
 * @return String
 */
public String getDayName()
{
	return getTOUDay().getDayName();
}
/**
 * Insert the method's description here.
 */
public void retrieve() throws java.sql.SQLException 
{
	getTOUDay().retrieve();

	java.util.Vector someDays = TOUDayRateSwitches.getAllDayRateSwitches(
				getTOUDay().getDayID(), getDbConnection() );

	for( int i = 0; i < someDays.size(); i++ )
		getTOURateSwitchVector().add( someDays.get(i) );
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getTOUDay().setDbConnection(conn);
	
	for( int i = 0; i < getTOURateSwitchVector().size(); i++ )
		((com.cannontech.database.db.tou.TOUDayRateSwitches)getTOURateSwitchVector().get(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 * @return com.cannontech.database.data.tou.TOUDay
 */
public void setNameID( Integer newID )
{
	getTOUDay().setDayID( newID );
	
	for( int i = 0; i < getTOURateSwitchVector().size(); i++ )
		((com.cannontech.database.db.tou.TOUDayRateSwitches)getTOURateSwitchVector().get(i)).setDayID(newID);
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 * @return com.cannontech.database.data.tou.TOUDay
 */
public void setNameName( String newName )
{
	getTOUDay().setDayName( newName );	
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM)
 */
public String toString()
{
	return getDayName();
}
/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:35:21 AM))
 */
public void update() throws java.sql.SQLException
{
	getTOUDay().update();

	//delete all the Dates
	TOUDayRateSwitches.deleteAllDayRateSwitches(getTOUDay().getDayID(), getDbConnection());

	for (int i = 0; i < getTOURateSwitchVector().size(); i++)
		((TOUDayRateSwitches) getTOURateSwitchVector().elementAt(i)).add();	
}

}

