package com.cannontech.database.db.state;

/**
 * This type was created in VisualAge.
 */
public class StateGroup extends com.cannontech.database.db.DBPersistent 
{
	private static final String TABLENAME = "StateGroup";
	
	private Integer stateGroupID = null;
	private String name = null;


	// ALWAYS UPDATE THIS VALUE TO THE MAX GROUPID WE USE IN HERE!!
	public static final int PREDEFINED_MAX_STATE_GROUPID = 3;
	
	// predefined uneditable stateGroups
	public static final int STATEGROUPID_CAPBANK = PREDEFINED_MAX_STATE_GROUPID;
	public static final int STATEGROUP_THREE_STATE_STATUS = 2;
	public static final int STATEGROUP_TWO_STATE_STATUS = 1;

	public static final int SYSTEM_STATEGROUPID = 0;
	
	public static final int STATEGROUP_ALARM = -5;
	public static final int STATEGROUP_CALCULATED = -3;
	public static final int STATEGROUP_ACCUMULATOR = -2;
	public static final int STATEGROUP_ANALOG = -1;
/**
 * StateGroup constructor comment.
 */
public StateGroup() {
	super();
	initialize( null, null );
}
/**
 * StateGroup constructor comment.
 */
public StateGroup(Integer stateGroupID) {
	super();
	initialize( stateGroupID, null );
}
/**
 * StateGroup constructor comment.
 */
public StateGroup(Integer stateGroupID, String name ) {
	super();
	initialize( stateGroupID, name );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object setValues[] = { getStateGroupID(), getName() };

	add( TABLENAME, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	String constraintColumns[] = { "STATEGROUPID" };
	String selectColumns[] = { "NAME", "STATEGROUPID" };
	Object constraintValues[] = { getStateGroupID() };

	delete( TABLENAME, constraintColumns, constraintValues );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextStateGroupID() {

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List stateGroups = cache.getAllStateGroups();
		java.util.Collections.sort(stateGroups);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<stateGroups.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteStateGroup)stateGroups.get(i)).getStateGroupID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getStateGroupID() {
	return stateGroupID;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.StateGroup[]
 */
public static final StateGroup[] getStateGroups() throws java.sql.SQLException {

	return getStateGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.StateGroup[]
 */
public static final StateGroup[] getStateGroups(String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT STATEGROUPID,NAME FROM " + TABLENAME;

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new StateGroup( 
						new Integer(rset.getInt("StateGroupID")), 
						rset.getString("Name") ) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//something is up
		}	
	}


	StateGroup retVal[] = new StateGroup[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param name java.lang.String
 */
protected void initialize( Integer stateGroupID, String name) {

	setStateGroupID( stateGroupID );
	setName( name );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String constraintColumns[] = { "STATEGROUPID" };
	String selectColumns[] = { "NAME", "STATEGROUPID" };
	Object constraintValues[] = { getStateGroupID() };

	Object results[] = retrieve( selectColumns, TABLENAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setName( (String) results[0] ); 		
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results returned" );

	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setName(String newValue) {
	this.name = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setStateGroupID(Integer newValue) {
	this.stateGroupID = newValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "NAME" };
	String constraintColumns[] = { "STATEGROUPID" };
	String selectColumns[] = { "NAME", "STATEGROUPID" };
	Object constraintValues[] = { getStateGroupID() };
	Object setValues[] = { getName() };

	update( TABLENAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
