/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.tou;

import com.cannontech.database.db.tou.TOURateOffset;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUSchedule extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.tou.TOUSchedule touSchedule = null;

	//objects of type com.cannontech.database.db.tou.TOURateOffset will only go in here
	private java.util.Vector rateOffsetsVector = null;
/**
 * TOUSchedule constructor comment.
 */
public TOUSchedule() {
	super();
}
/**
 * TOUSchedule constructor comment.
 */
public TOUSchedule(Integer id)
{
	super();

	setScheduleID(id);
}
/**
 * TOUSchedule constructor comment.
 */
public TOUSchedule(Integer id, String name)
{
	super();

	setScheduleID(id);
	setScheduleName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 */
public void add() throws java.sql.SQLException 
{
	getTOUSchedule().add();
		
	for (int i = 0; i < getRateOffsetsVector().size(); i++)
		((TOURateOffset) getRateOffsetsVector().elementAt(i)).add();

}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:36:20 AM)
 */
public void delete() throws java.sql.SQLException 
{
	TOURateOffset.deleteAllRateOffsets(getTOUSchedule().getScheduleID(), getDbConnection());

	getTOUSchedule().delete();	
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getScheduleID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TOU_SCHEDULE_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TOU_SCHEDULE,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_TOU_SCHEDULE,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getRateOffsetsVector()
{
	if (rateOffsetsVector == null)
		rateOffsetsVector = new java.util.Vector();

	return rateOffsetsVector;
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
private com.cannontech.database.db.tou.TOUSchedule getTOUSchedule()
{
	if (touSchedule == null)
		touSchedule = new com.cannontech.database.db.tou.TOUSchedule();

	return touSchedule;
}
/**
 * Insert the method's description here.
 * @return Integer
 */
public Integer getScheduleID()
{
	return getTOUSchedule().getScheduleID();
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 12:21:32 PM)
 * @return String
 */
public String getScheduleName()
{
	return getTOUSchedule().getScheduleName();
}
/**
 * Insert the method's description here.
 */
public void retrieve() throws java.sql.SQLException 
{
	getTOUSchedule().retrieve();

	java.util.Vector rateOffsets = TOURateOffset.getAllRateOffsets(
				getTOUSchedule().getScheduleID(), getDbConnection() );

	for( int i = 0; i < rateOffsets.size(); i++ )
		getRateOffsetsVector().add( rateOffsets.get(i) );


}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getTOUSchedule().setDbConnection(conn);
	
	for( int i = 0; i < getRateOffsetsVector().size(); i++ )
		((com.cannontech.database.db.tou.TOURateOffset)getRateOffsetsVector().get(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
public void setScheduleID( Integer newID )
{
	getTOUSchedule().setScheduleID( newID );
	
	for( int i = 0; i < getRateOffsetsVector().size(); i++ )
		((com.cannontech.database.db.tou.TOURateOffset)getRateOffsetsVector().get(i)).setTOUScheduleID(newID);
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
public void setScheduleName( String newName )
{
	getTOUSchedule().setScheduleName( newName );	
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 */
public String toString()
{
	return getScheduleName();
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM))
 */
public void update() throws java.sql.SQLException
{
	getTOUSchedule().update();

	//delete all the Dates
	com.cannontech.database.db.tou.TOURateOffset.deleteAllRateOffsets(getTOUSchedule().getScheduleID(), getDbConnection());

	for (int i = 0; i < getRateOffsetsVector().size(); i++)
		((TOURateOffset) getRateOffsetsVector().elementAt(i)).add();	
}
}

