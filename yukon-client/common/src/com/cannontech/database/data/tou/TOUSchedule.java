/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.tou;

import com.cannontech.database.db.tou.TOUDayMapping;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUSchedule extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.tou.TOUSchedule touSchedule = null;

	//only objects of type com.cannontech.database.db.tou.TOUDayMapping will go in here
	private java.util.Vector touDayMappingVector = null;
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
 * TOUSchedule constructor comment.
 */
public TOUSchedule(Integer id, String name, String rate)
{
	super();

	setScheduleID(id);
	setScheduleName(name);
	setDefaultRate(rate);
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 */
public void add() throws java.sql.SQLException 
{
	if(getScheduleID() == null)
		setScheduleID(com.cannontech.database.db.tou.TOUSchedule.getNextTOUScheduleID());
	
	getTOUSchedule().add();
	
	for (int i = 0; i < getTOUDayMappingVector().size(); i++)
	{
		((TOUDayMapping) getTOUDayMappingVector().elementAt(i)).setScheduleID(getTOUSchedule().getScheduleID());
		((TOUDayMapping) getTOUDayMappingVector().elementAt(i)).add();
	}
	
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:36:20 AM)
 */
public void delete() throws java.sql.SQLException 
{
	java.util.Vector vectorIDs = TOUDayMapping.getAllDayIDList(getTOUSchedule().getScheduleID(), getDbConnection());
	TOUDayMapping.deleteAMapping(getTOUSchedule().getScheduleID(), getDbConnection());

	for (int i = 0; i < vectorIDs.size(); i++)
	{
		TOUDay newDayDawning = new TOUDay();
		newDayDawning.setDayID((Integer)vectorIDs.elementAt(i));
		newDayDawning.setDbConnection(this.getDbConnection());
		newDayDawning.delete();		
	}

	getTOUSchedule().delete();	
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
    
    DBChangeMsg[] msgs = {
            new DBChangeMsg(
                            getScheduleID().intValue(),
                            DBChangeMsg.CHANGE_TOU_SCHEDULE_DB,
                            DBChangeMsg.CAT_TOU_SCHEDULE,
                            DBChangeMsg.CAT_TOU_SCHEDULE,
                            dbChangeType)
	};
	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getTOUDayMappingVector()
{
	if (touDayMappingVector == null)
		touDayMappingVector = new java.util.Vector();

	return touDayMappingVector;
}
/**
 * Insert the method's description here.
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
public com.cannontech.database.db.tou.TOUSchedule getTOUSchedule()
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
 * Creation date: (9/22/2004 12:21:32 PM)
 * @return String
 */
public String getDefaultRate()
{
	return getTOUSchedule().getDefaultRate();
}
/**
 * Insert the method's description here.
 */
public void retrieve() throws java.sql.SQLException 
{
	getTOUSchedule().retrieve();

	java.util.Vector someDays = TOUDayMapping.getAllScheduleDays(
				getTOUSchedule().getScheduleID(), getDbConnection() );

	for( int i = 0; i < someDays.size(); i++ )
		getTOUDayMappingVector().add( someDays.get(i) );
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
	
	for( int i = 0; i < getTOUDayMappingVector().size(); i++ )
		((com.cannontech.database.db.tou.TOUDayMapping)getTOUDayMappingVector().get(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:35:21 AM)
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
public void setScheduleID( Integer newID )
{
	getTOUSchedule().setScheduleID( newID );
	
	for( int i = 0; i < getTOUDayMappingVector().size(); i++ )
		((com.cannontech.database.db.tou.TOUDayMapping)getTOUDayMappingVector().get(i)).setScheduleID(newID);
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
 * @return com.cannontech.database.data.tou.TOUSchedule
 */
public void setDefaultRate( String defaultRate_)
{
	getTOUSchedule().setDefaultRate( defaultRate_ );	
}

public void setTOUDayMappingVector(java.util.Vector newDayMapping)
{
	touDayMappingVector = newDayMapping;
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

	//delete all the days
	TOUDayMapping.deleteAMapping(getTOUSchedule().getScheduleID(), getDbConnection());

	for (int i = 0; i < getTOUDayMappingVector().size(); i++)
		((TOUDayMapping) getTOUDayMappingVector().elementAt(i)).add();	
}



/*
public final static boolean inUseByDevice(Integer touID) throws java.sql.SQLException 
{	
	return inUseByDevice(touID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}



public final static boolean inUseByDevice(Integer schedID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT DeviceID FROM TOUDeviceMapping WHERE TOUScheduleID=" + schedID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}*/
}

