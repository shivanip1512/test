package com.cannontech.database.data.state;

/**
 * This type was created in VisualAge.
 */
public final class StateFactory {
/**
 * This method was created in VisualAge.
 */
public final static GroupState createGroupState() {

	GroupState returnGroupState = null;
	
	try
	{
		returnGroupState = createGroupState( com.cannontech.database.db.state.StateGroup.getNextStateGroupID() );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	return returnGroupState;
}
/**
 * This method was created in VisualAge.
 */
public final static GroupState createGroupState( Integer stateGroupID ) {
 
	GroupState returnGroupState = new GroupState( stateGroupID );
	return returnGroupState;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.state.GroupState[]
 */
public final static GroupState[] getAllGroupStates() throws java.sql.SQLException {

	com.cannontech.database.db.state.StateGroup stateGroups[]  =  com.cannontech.database.db.state.StateGroup.getStateGroups();

	java.util.Vector allGroupStatesV = new java.util.Vector();
	
	for( int i = 0; i < stateGroups.length; i++ )
	{
		GroupState groupState = getGroupState( stateGroups[i].getStateGroupID() );
		allGroupStatesV.addElement( groupState );
	}

	GroupState allGroupStates[] = new GroupState[allGroupStatesV.size()];

	allGroupStatesV.copyInto(allGroupStates);

	return allGroupStates;	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.route.RouteBase
 * @param name java.lang.String
 */
public final static GroupState getGroupState(Integer stateGroupID)
{
	GroupState returnGroupState = null; 

	returnGroupState = createGroupState( stateGroupID );
	try
	{
		com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, returnGroupState);
		returnGroupState = (GroupState)t.execute();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	return returnGroupState;
}
}
