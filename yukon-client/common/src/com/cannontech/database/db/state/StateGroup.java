package com.cannontech.database.db.state;


/**
 * This type was created in VisualAge.
 */
public class StateGroup extends com.cannontech.database.db.DBPersistent 
{	
	private Integer stateGroupID = null;
	private String name = null;
   private String groupType = StateGroupUtils.GROUP_TYPE_STATUS;
   

   public static final String SETTER_COLUMNS[] = 
   { 
      "Name", "GroupType"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "StateGroupID" };

   private static final String TABLE_NAME = "StateGroup";
/**
 * StateGroup constructor comment.
 */
public StateGroup() 
{
	super();
}
/**
 * StateGroup constructor comment.
 */
public StateGroup(Integer stateGroupID) {
	super();
	initialize( stateGroupID, null, null );
}
/**
 * StateGroup constructor comment.
 */
public StateGroup(Integer stateGroupID, String name, String groupType_ ) 
{
	super();
	initialize( stateGroupID, name, groupType_ );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object setValues[] = { getStateGroupID(), getName(), getGroupType() };

	add( TABLE_NAME, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getStateGroupID() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return name;
}
public final static Integer getNextStateGroupID() throws java.sql.SQLException 
{	
	return getNextStateGroupID(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}

public final static Integer getNextStateGroupID(String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT Max(StateGroupID)+1 FROM " + TABLE_NAME ,
													databaseAlias );

	try
	{
		stmt.execute();
		return new Integer(stmt.getRow(0)[0].toString());
	}
	catch( Exception e )
	{
	   com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	   return new Integer(-5);
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

	String sql = "SELECT STATEGROUPID,NAME,GroupType FROM " + TABLE_NAME;

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
						rset.getString("Name"),
                  rset.getString("GroupType") ) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
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
private void initialize( Integer stateGroupID, String name, String groupType_) 
{
	setStateGroupID( stateGroupID );
	setName( name );
   setGroupType( groupType_ );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getStateGroupID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setName( (String) results[0] );
      setGroupType( (String) results[1] );
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
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getStateGroupID() };
	Object setValues[] = 
   { 
      getName(), getGroupType()
   };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the groupType.
	 * @return String
	 */
	public String getGroupType()
	{
		return groupType;
	}

	/**
	 * Sets the groupType.
	 * @param groupType The groupType to set
	 */
	public void setGroupType(String groupType)
	{
		this.groupType = groupType;
	}

}
