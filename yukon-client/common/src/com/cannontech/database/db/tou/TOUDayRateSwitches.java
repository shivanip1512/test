/*
 * Created on Dec 06, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tou;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDayRateSwitches extends com.cannontech.database.db.DBPersistent 
{
	private Integer rateSwitchID;
	private String switchRate;
	private Integer switchOffset;
	private Integer dayID;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"SWITCHRATE", "SWITCHOFFSET", "TOUDAYID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "TOURateSwitchID" };

	public static final String TABLE_NAME = "TOUDayRateSwitches";

/**
 * TOURateOffset constructor comment.
 */
public TOUDayRateSwitches() {
	super();
}
/**
 * TOURateOffset constructor comment.
 */
public TOUDayRateSwitches(String rate, Integer offset) 
{
	super();
	switchRate = rate;
	switchOffset = offset;
}

public TOUDayRateSwitches(Integer rateID, String rate, Integer offset, Integer touDayID) 
{
	super();

	rateSwitchID = rateID;
	switchRate = rate;
	switchOffset = offset;
	dayID = touDayID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:34:05 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getRateSwitchID(), getSwitchRate(),
		getSwitchOffset(), getDayID()
	};

	add( TABLE_NAME, addValues );
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:34:22 AM)
 */
public void delete() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllDayRateSwitches(Integer touDayID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt = null;

	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	try
	{
		java.sql.Statement stat = conn.createStatement();

		stat.execute("DELETE FROM " + TABLE_NAME + " WHERE TOUDayID=" + touDayID);

		if (stat != null)
			stat.close();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 12:52:15 PM)
 * @param touScheduleID java.lang.Integer
 * @param dbAlias java.lang.String
 */
public static final java.util.Vector getAllDayRateSwitches(Integer touDayID, java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector(5);
	Integer rateSwitchID = null;
	String 	switchRate = null;
	Integer switchOffset = null;
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + SETTER_COLUMNS[1] +"," 
		+ SETTER_COLUMNS[2] + 
		" FROM " + TABLE_NAME +
		" WHERE " + CONSTRAINT_COLUMNS[0] + 
		"=? ORDER BY " + SETTER_COLUMNS[1];

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, rateSwitchID.intValue() );
			
			rset = pstmt.executeQuery();
	
			while( rset.next() )
			{
				rateSwitchID = new Integer( rset.getInt(SETTER_COLUMNS[0]) );
				switchRate = rset.getString(SETTER_COLUMNS[1]);
				switchOffset = new Integer( rset.getInt(SETTER_COLUMNS[2]) );
								
				returnVector.addElement( new TOUDayRateSwitches(
						rateSwitchID, 
						switchRate, 
						switchOffset, touDayID) );				
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
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return returnVector;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRateSwitchID() {
	return rateSwitchID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSwitchOffset() {
	return switchOffset;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 5:40:28 PM)
 * @return java.lang.String
 */
public java.lang.String getSwitchRate() {
	return switchRate;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDayID() {
	return dayID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:33:53 AM)
 */
public void retrieve() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}

/**
 * Insert the method's description here.
 * @param newRateSwitchID java.lang.Integer
 */
public void setRateSwitchID(java.lang.Integer newRateSwitchID) {
	rateSwitchID = newRateSwitchID;
}

/**
 * Insert the method's description here.
 * @param newSwitchOffset java.lang.Integer
 */
public void setSwitchOffset(java.lang.Integer newSwitchOffset) {
	switchOffset = newSwitchOffset;
}

/**
 * Insert the method's description here.
 * @param newSwitchRate java.lang.String
 */
public void setSwitchRate(java.lang.String newSwitchRate) {
	switchRate = newSwitchRate;
}

/**
 * Insert the method's description here.
 * @param newDayID java.lang.Integer
 */
public void setDayID(java.lang.Integer newDayID) {
	dayID = newDayID;
}

/**
 * Insert the method's description here.
 * Creation date: (12/06/2004 10:34:35 AM)
 */
public void update() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
}
