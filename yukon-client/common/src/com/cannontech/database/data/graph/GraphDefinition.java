package com.cannontech.database.data.graph;

import java.util.ArrayList;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.graph.GraphCustomerList;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.web.OperatorLoginGraphList;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 10:50:03 AM)
 * @author: 
 */
public class GraphDefinition extends DBPersistent implements CTIDbChange
{
	private com.cannontech.database.db.graph.GraphDefinition graphDefinition;
	private ArrayList graphDataSeries;
/**
 * GraphDefinition constructor comment.
 */
public GraphDefinition() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	ArrayList list = new ArrayList(10);

	//add the basic change method
	list.add( new DBChangeMsg(
					getGraphDefinition().getGraphDefinitionID().intValue(),
					DBChangeMsg.CHANGE_GRAPH_DB,
					DBChangeMsg.CAT_GRAPH,
					DBChangeMsg.CAT_GRAPH,
					typeOfChange ) );
	 
	DBChangeMsg[] dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg[list.size()];
	return (DBChangeMsg[])list.toArray( dbChange );
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getGraphDefinition().setDbConnection( getDbConnection() );
	getGraphDefinition().add();
	getGraphDefinition().setDbConnection(null);

	java.util.Iterator iter = getGraphDataSeries().iterator();

	while( iter.hasNext() )
	{
		GraphDataSeries gds = (GraphDataSeries) iter.next();
		gds.setGraphDefinitionID( getGraphDefinition().getGraphDefinitionID() );
		gds.setDbConnection( getDbConnection() );
		gds.add();
		gds.setDbConnection( null );
	}
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{	
	delete(OperatorLoginGraphList.tableName, "GraphDefinitionID", getGraphDefinition().getGraphDefinitionID());
	delete(GraphCustomerList.TABLE_NAME, "GraphDefinitionID", getGraphDefinition().getGraphDefinitionID());
	GraphDataSeries.deleteAllGraphDataSeries(getGraphDefinition().getGraphDefinitionID());

	getGraphDefinition().setDbConnection(getDbConnection());
	getGraphDefinition().delete();
	getGraphDefinition().setDbConnection(null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:51:05 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getGraphDataSeries() {
	if( graphDataSeries == null )
		graphDataSeries = new java.util.ArrayList(8);
		
	return graphDataSeries;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:50:28 AM)
 * @return com.cannontech.database.db.graph.GraphDefinition
 */
public com.cannontech.database.db.graph.GraphDefinition getGraphDefinition() {
	if( graphDefinition == null )
		graphDefinition = new com.cannontech.database.db.graph.GraphDefinition();
		
	return graphDefinition;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getGraphDefinition().setDbConnection(getDbConnection());
	getGraphDefinition().retrieve();

	Object[] gds = GraphDataSeries.getAllGraphDataSeries( getGraphDefinition().getGraphDefinitionID(), getDbConnection().toString() );

	for( int i = 0; i < gds.length; i++ )
		getGraphDataSeries().add( gds[i] );
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:51:05 AM)
 * @param newGraphDataSeries java.util.ArrayList
 */
public void setGraphDataSeries(java.util.ArrayList newGraphDataSeries) {
	graphDataSeries = newGraphDataSeries;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:50:28 AM)
 * @param newGraphDefinition com.cannontech.database.db.graph.GraphDefinition
 */
public void setGraphDefinition(com.cannontech.database.db.graph.GraphDefinition newGraphDefinition) {
	graphDefinition = newGraphDefinition;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	getGraphDefinition().setDbConnection(getDbConnection());
	getGraphDefinition().update();
	getGraphDefinition().setDbConnection(null);
	
	GraphDataSeries.deleteAllGraphDataSeries(getGraphDefinition().getGraphDefinitionID());

	java.util.List gds = getGraphDataSeries();	
	if( gds != null )
	for( int i = 0; i < gds.size(); i++ ) 
	{
		DBPersistent elem = (DBPersistent) gds.get(i);
		elem.setDbConnection(getDbConnection());
		elem.add();
		elem.setDbConnection(null);
	}
}
}
