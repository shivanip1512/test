package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.DynamicAccumulator;
import com.cannontech.database.db.point.DynamicPointDispatch;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.database.db.point.fdr.FDRTranslation;

public class PointBase extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private Point point = null;
	private Vector pointFDRVector = null;
	private PointAlarming pointAlarming = null;
	private boolean isPartialDelete;
	
/**
 * PointBase constructor comment.
 */
public PointBase() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getPoint().getPointID() == null )
		setPointID( new Integer(com.cannontech.database.db.point.Point.getNextPointID(getDbConnection())) );

	getPoint().add();

	for( int i = 0 ; i < getPointFDRVector().size(); i++ )
		((com.cannontech.database.db.point.fdr.FDRTranslation) getPointFDRVector().elementAt(i)).add();

	// add a PointAlarming row for each point created
	getPointAlarming().setPointID(getPoint().getPointID());
	
	//All the data below is now the default values for a PointAlarm instance 5-8-2002 RWN*/	
/*	getPointAlarming().setAlarmStates(PointAlarming.DEFAULT_ALARM_STATES);
	getPointAlarming().setExcludeNotifyStates(PointAlarming.DEFAULT_EXCLUDE_NOTIFY);
	getPointAlarming().setNotifyOnAcknowledge("N");
	getPointAlarming().setNotificationGroupID( new Integer(PointAlarming.NONE_NOTIFICATIONID));
	getPointAlarming().setRecipientID( new Integer(PointAlarming.NONE_LOCATIONID));
*/
	getPointAlarming().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:47:42 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	setPointID(getPoint().getPointID());

}

/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	//ADD TABLES THAT HAVE A REFERENCE TO THE POINT TABLE AND THAT
	// NEED TO BE DELETED WHEN A POINT ROW IS DELETED (CASCADE DELETE)
	delete(FDRTranslation.TABLE_NAME, "PointID", getPoint().getPointID());
	delete(DynamicPointDispatch.TABLE_NAME, "PointID", getPoint().getPointID());
	delete(DynamicAccumulator.TABLE_NAME, "PointID", getPoint().getPointID());
	delete(GraphDataSeries.tableName, "PointID", getPoint().getPointID());
	delete("DynamicPointAlarming", "PointID", getPoint().getPointID());
	//delete(PointControl.TABLE_NAME, "PointID", getPoint().getPointID());

	//A TDC Table that does not have a DBPersistant
	delete("Display2WayData", "PointID", getPoint().getPointID());
	if (!isPartialDelete)
	{
		getPointAlarming().delete();
		delete(RawPointHistory.TABLE_NAME, "PointID", getPoint().getPointID());
		delete(SystemLog.TABLE_NAME, "PointID", getPoint().getPointID());
		getPoint().delete();
	}

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:48:04 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	isPartialDelete = true;
	this.delete();
	isPartialDelete = false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj com.cannontech.database.data.point.PointBase
 */
public boolean equals(PointBase obj) {
	return getPoint().getPointID().equals( obj.getPoint().getPointID() );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj com.cannontech.database.data.point.PointBase
 */
public boolean equals(Object obj) {

	if( obj instanceof PointBase )
		return getPoint().getPointID().equals( ((PointBase) obj).getPoint().getPointID() );
	else
		return super.equals( obj );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getPoint().getPointID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_POINT_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_POINT,
					getPoint().getPointType(),
					typeOfChange)
	};


	return msgs;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.Point
 */
public Point getPoint() {
	if( this.point == null )
		this.point = new Point();
		
	return this.point;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 1:34:10 PM)
 * @return com.cannontech.database.db.point.PointAlarming
 */
public com.cannontech.database.db.point.PointAlarming getPointAlarming() 
{
	if( pointAlarming == null )
		pointAlarming = new PointAlarming();
		
	return pointAlarming;
}
/**
 * This method was created in VisualAge.
 */
public Vector getPointFDRVector() {

	if( pointFDRVector == null )
		pointFDRVector = new Vector();
	
	return pointFDRVector;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCapBank(Integer pointID) throws java.sql.SQLException 
{	
	return hasCapBank(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCapBank(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT count(ControlPointID) FROM " + 
               com.cannontech.database.db.capcontrol.CapBank.TABLE_NAME + 
               " WHERE ControlPointID=" + pointID, databaseAlias );

	try
	{
		stmt.execute();
      return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCapControlSubstationBus(Integer pointID) throws java.sql.SQLException 
{	
	return hasCapControlSubstationBus(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCapControlSubstationBus(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
           "SELECT count(CurrentWattLoadPointID) FROM " + 
           com.cannontech.database.db.capcontrol.CapControlSubstationBus.TABLE_NAME + 
			  " WHERE CurrentWattLoadPointID=" + pointID + 
			  " or CurrentVarLoadPointID=" + pointID, databaseAlias );

	try
	{
		stmt.execute();
      return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasLMGroup(Integer pointID) throws java.sql.SQLException 
{	
	return hasLMGroup(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasLMGroup(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT count(PointIDUsage) FROM " + com.cannontech.database.db.device.lm.LMGroupPoint.TABLE_NAME + 
			  " WHERE PointIDUsage=" + pointID, databaseAlias );

	try
	{
		stmt.execute();
      return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasLMTrigger(Integer pointID) throws java.sql.SQLException 
{	
	return hasLMTrigger(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasLMTrigger(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT count(PointID) FROM " + com.cannontech.database.db.device.lm.LMControlAreaTrigger.TABLE_NAME + 
			  " WHERE PointID=" + pointID + 
			  " or PeakPointID=" + pointID, databaseAlias );

	try
	{
		stmt.execute();
      return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasRawPointHistorys(Integer pointID) throws java.sql.SQLException {
	
	return hasRawPointHistorys(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasRawPointHistorys(Integer pointID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT count(pointID) FROM " + RawPointHistory.TABLE_NAME + " WHERE pointID=" + pointID,
													databaseAlias );

	try
	{
		stmt.execute();
		return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:17:59 AM)
 * @return boolean
 * @param ptID java.lang.Integer
 */
public static final boolean hasSystemLogEntry(Integer ptID) throws java.sql.SQLException
{
	return hasSystemLogEntry( ptID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasSystemLogEntry(Integer ptID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT count(pointID) FROM " + SystemLog.TABLE_NAME + " WHERE pointID=" + ptID,
													databaseAlias );

	try
	{
		stmt.execute();
      return new Integer(stmt.getRow(0)[0].toString()).intValue() > 0;
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	getPoint().retrieve();
	getPointAlarming().retrieve();
		
	setPointFDRVector(com.cannontech.database.db.point.fdr.FDRTranslation.getFDRTranslations(getPoint().getPointID()));
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPoint().setDbConnection(conn);

	getPointAlarming().setDbConnection(conn);

	for( int i = 0 ; i < getPointFDRVector().size(); i++ )
		((com.cannontech.database.db.point.fdr.FDRTranslation) getPointFDRVector().elementAt(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.Point
 */
public void setPoint(Point newValue) {
	this.point = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 1:34:10 PM)
 * @param newPointAlarm com.cannontech.database.db.point.PointAlarming
 */
public void setPointAlarming(com.cannontech.database.db.point.PointAlarming newPointAlarming) {
	pointAlarming = newPointAlarming;
}
/**
 * This method was created in VisualAge.
 */
public void setPointFDRVector(Vector newValue) {
	this.pointFDRVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newID java.lang.Integer
 */
public void setPointID(Integer newID) {

	getPoint().setPointID(newID);

	getPointAlarming().setPointID(newID);
	
	for( int i = 0 ; i < getPointFDRVector().size(); i++ )
		((com.cannontech.database.db.point.fdr.FDRTranslation) getPointFDRVector().elementAt(i)).setPointID(newID);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getPoint().getPointName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	getPoint().update();
	
	getPointAlarming().update();

	//delete all the current FDRTranslations, then add the new ones
	delete( FDRTranslation.TABLE_NAME, "PointID", getPoint().getPointID() );
	for( int i = 0 ; i < getPointFDRVector().size(); i++ )
		((com.cannontech.database.db.point.fdr.FDRTranslation) getPointFDRVector().elementAt(i)).add();
}
}
