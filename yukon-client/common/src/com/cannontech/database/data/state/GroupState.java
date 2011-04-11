package com.cannontech.database.data.state;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * This type was created in VisualAge.
 */

public class GroupState extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.state.StateGroup stateGroup = null;
   
   //contains instances of com.cannontech.database.data.state.State
	private java.util.Vector statesVector = null;

/**
 * StatusPoint constructor comment.
 */
public GroupState() {
	super();
}
/**
 * StatusPoint constructor comment.
 */
public GroupState(Integer stateGroupID) {
	super();
	getStateGroup().setStateGroupID(stateGroupID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	
	getStateGroup().add();

	if( getStatesVector() != null )
		for( int i = 0; i < getStatesVector().size(); i++ )
			((com.cannontech.database.db.DBPersistent) getStatesVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	com.cannontech.database.db.state.State.deleteAllStates( getStateGroup().getStateGroupID(), getDbConnection() );
	
	getStateGroup().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
	
    DBChangeMsg[] msgs = {
            new DBChangeMsg(
                            getStateGroup().getStateGroupID().intValue(),
                            DBChangeMsg.CHANGE_STATE_GROUP_DB,
                            DBChangeMsg.CAT_STATEGROUP,
                            DBChangeMsg.CAT_STATEGROUP,
                            dbChangeType)
	};

	return msgs;
}

public String getGroupType() {
        
    return getStateGroup().getGroupType();
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointControl
 */
public com.cannontech.database.db.state.StateGroup getStateGroup() {
	if( stateGroup == null )
		stateGroup = new com.cannontech.database.db.state.StateGroup();
		
	return stateGroup;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointControl
 */
public java.util.Vector getStatesVector() {
	if( statesVector == null )
		statesVector = new java.util.Vector();
		
	return statesVector;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPoint(Integer sGroupID) throws java.sql.SQLException 
{	
	return hasPoint(sGroupID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPoint(Integer sGroupID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT StateGroupID FROM " + com.cannontech.database.db.point.Point.TABLE_NAME + " WHERE StateGroupID=" + sGroupID,
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
}


/**
 * Helper method to return true if stateGroupId is referenced by a "monitor".
 *  ("monitor" includes PorterResponseMonitor and StatusPointMonitor).
 * @param stateGroupId
 * @return
 */
public final static boolean hasMonitor(Integer stateGroupId) 
{
	JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate(); 
	SqlStatementBuilder sql = new SqlStatementBuilder();
	sql.append("SELECT MonitorId FROM PorterResponseMonitor PRM");
	sql.append("WHERE PRM.StateGroupId").eq(stateGroupId);
	sql.append("UNION SELECT StatusPointMonitorId FROM StatusPointMonitor SPM");
	sql.append("WHERE SPM.StateGroupId").eq(stateGroupId);

    List<Integer> monitorIds = yukonTemplate.queryForList(sql.toString(), sql.getArguments(), Integer.class);

    return !monitorIds.isEmpty();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException {

	getStateGroup().retrieve();
	statesVector = new java.util.Vector();
	
	try
	{
		com.cannontech.database.db.state.State rArray[] = com.cannontech.database.db.state.State.getStates( getStateGroup().getStateGroupID() );

		for( int i = 0; i < rArray.length; i++ )
		{
         State stateData = new State();
         stateData.setState( rArray[i] );
         
			stateData.setDbConnection( getDbConnection() );
			statesVector.addElement( stateData );
		}
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}

	for( int i = 0; i < getStatesVector().size(); i++ )
		((com.cannontech.database.db.DBPersistent) getStatesVector().elementAt(i)).retrieve();

}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getStateGroup().setDbConnection(conn);
	
	java.util.Vector v = getStatesVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
			((com.cannontech.database.db.DBPersistent) v.elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
public void setStateGroup(com.cannontech.database.db.state.StateGroup newValue) {
	this.stateGroup = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void setStateGroupID(Integer stateGroupID) {
	getStateGroup().setStateGroupID(stateGroupID);
	if( getStatesVector() != null )
			for( int i = 0; i < getStatesVector().size(); i++ )
				{
					State state = (State) getStatesVector().get(i);
					state.getState().setStateGroupID(getStateGroup().getStateGroupID());
				}
	
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
public void setStatesVector(java.util.Vector newValue) {
	this.statesVector = newValue;
}

public void setGroupType(String groupType)
{
    getStateGroup().setGroupType(groupType);
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {

	if( getStateGroup() != null )
		return getStateGroup().getName();
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException {
	
	getStateGroup().update();

	com.cannontech.database.db.state.State.deleteAllStates( getStateGroup().getStateGroupID(), getDbConnection() );

	if( getStatesVector() != null )
		for( int i = 0; i < getStatesVector().size(); i++ )
			((com.cannontech.database.db.DBPersistent) getStatesVector().elementAt(i)).add();
}
}
