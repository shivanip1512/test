package com.cannontech.database.db.state;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class State extends com.cannontech.database.db.DBPersistent 
{
   // ANY is primarily used for the Initial State and Normal State values in PointStatus
   public static final String ANY = "Any";
   public static final int ANY_ID = -1;
   
	private Integer stateGroupID = null;
	private Integer rawState = null;
	private String text = null;
	private Integer foregroundColor = null;
	private Integer backgroundColor = null;
   private Integer imageID = new Integer(YukonImage.NONE_IMAGE_ID); //default to no image
	

	
   public static final String SETTER_COLUMNS[] = 
   { 
      "STATEGROUPID", "RAWSTATE", "TEXT", "FOREGROUNDCOLOR",
      "BACKGROUNDCOLOR", "IMAGEID"
   };

   public static final String CONSTRAINT_COLUMNS[] = { "StateGroupID", "RawState" };
   
   public static final String TABLE_NAME = "State";
   
   
/**
 * State constructor comment.
 */
public State() 
{
	super();
}

/**
 * State constructor comment.
 */
public State(Integer stateGroupID, Integer rawState, 
   String text, Integer foregroundColor, Integer backgroundColor, Integer imageID_) 
{
	super();
	initialize( stateGroupID, rawState, text, foregroundColor, backgroundColor, imageID_ );
}

public State(Integer stateGroupID, Integer rawState, 
   String text, Integer foregroundColor, Integer backgroundColor ) 
{
   super();
   initialize( stateGroupID, rawState, text, foregroundColor, backgroundColor, new Integer(0) );
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object setValues[] = 
   { 
      getStateGroupID(), getRawState(), getText(), 
      getForegroundColor(), getBackgroundColor(), getImageID()
   };

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
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static void deleteAllStates(Integer stateGroupID, java.sql.Connection conn )
{
	java.sql.PreparedStatement pstmt = null;

	String sql = "DELETE FROM " + State.TABLE_NAME +
				 	 " WHERE STATEGROUPID= ?";

	try
	{		

		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement( sql.toString() );
			pstmt.setInt( 1, stateGroupID.intValue() );			
			pstmt.executeUpdate();         
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
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getBackgroundColor() {
	return backgroundColor;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getForegroundColor() {
	return foregroundColor;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRawState() {
	return rawState;
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
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final State[] getStates(Integer stateGroup) throws java.sql.SQLException {

	return getStates(stateGroup, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
													
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final State[] getStates(Integer stateGroup, String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT RAWSTATE,TEXT,FOREGROUNDCOLOR,BACKGROUNDCOLOR,IMAGEID " + 
				 "FROM STATE WHERE STATE.STATEGROUPID= ? " +
				 "AND STATE.RAWSTATE > ? ORDER BY RAWSTATE";

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
			pstmt.setInt( 1, stateGroup.intValue() );
			pstmt.setInt( 2, State.ANY_ID );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new State(
						stateGroup, 
						new Integer(rset.getInt("RawState")), 
						rset.getString("Text"), 
						new Integer(rset.getInt("ForegroundColor")), 
						new Integer(rset.getInt("BackgroundColor")),
                  new Integer(rset.getInt("ImageID")) ) );                  
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
	}


	State retVal[] = new State[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getText() {
	return text;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param rawState java.lang.Integer
 * @param text java.lang.String
 * @param foregroundColor java.lang.Integer
 * @param backgoundColor java.lang.Integer
 */
public void initialize(
      Integer stateGroupID, Integer rawState, String text, 
      Integer foregroundColor, Integer backgroundColor, Integer imageID_ ) 
{

	setStateGroupID(stateGroupID);
	setRawState(rawState);
	setText(text);
	setForegroundColor(foregroundColor);
	setBackgroundColor(backgroundColor);
   setImageID( imageID_ );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getStateGroupID(), getRawState() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setStateGroupID( (Integer) results[0] );
		setRawState( (Integer) results[1] );
		setText( (String) results[2] );
		setForegroundColor( (Integer) results[3] );
		setBackgroundColor( (Integer) results[4] );
      setImageID( (Integer) results[5] );
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setBackgroundColor(Integer newValue) {
	this.backgroundColor = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setForegroundColor(Integer newValue) {
	this.foregroundColor = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRawState(Integer newValue) {
	this.rawState = newValue;
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
 * @param newValue java.lang.String
 */
public void setText(String newValue) {
	this.text = newValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getText().toString();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getStateGroupID(), getRawState() };
	Object setValues[] = 
   { 
      getText(), getForegroundColor(), getBackgroundColor(),
      getImageID()
   };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the imageID.
	 * @return Integer
	 */
	public Integer getImageID()
	{
		return imageID;
	}

	/**
	 * Sets the imageID.
	 * @param imageID The imageID to set
	 */
	public void setImageID(Integer imageID)
	{
		this.imageID = imageID;
	}

}
