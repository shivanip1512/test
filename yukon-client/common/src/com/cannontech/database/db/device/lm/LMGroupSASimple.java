/*
 * Created on Feb 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device.lm;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMGroupSASimple extends com.cannontech.database.db.DBPersistent 
{
	private Integer groupID;
	private Integer routeID;
	private String operationalAddress;
	private Integer nominalSwitchTimeout;
	private Integer virtualSwitchTimeout;
	

	public static final String SETTER_COLUMNS[] = 
	{ 
		"GROUPID", "ROUTEID", "OPERATIONALADDRESS", 
		"NOMINALSWITCHTIMEOUT", "VIRTUALSWITCHTIMEOUT"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "groupID" };

	public static final String TABLE_NAME = "LMGroupSASimple";

/**
 * Baseline constructor comment.
 */
public LMGroupSASimple() {
	super();
}
/**
 * Baseline constructor comment.
 */
public LMGroupSASimple(Integer grID, Integer rID) {
	super();
	groupID = grID;
	routeID = rID;

	
}


public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getGroupID(), getRouteID(),
		getOperationalAddress(),
		getNominalSwitchTimeout(),
		getVirtualSwitchTimeout()
		
	};

	add( TABLE_NAME, addValues );
}


public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getGroupID());
}



public static synchronized Integer getNextGroupID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(GroupID)+1 FROM " + TABLE_NAME );	
				
			 //get the first returned result
			 rset.next();
			return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if ( stmt != null) stmt.close();
			}
			catch (java.sql.SQLException e2) 
			{
				e2.printStackTrace();
			}
		}
		
		//strange, should not get here
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	}


public Integer getGroupID() {
	return groupID;
}

public Integer getRouteID() {
	return routeID;
}

public String getOperationalAddress() {
	return operationalAddress;
}

public Integer getNominalSwitchTimeout() {
	return nominalSwitchTimeout;
}

public Integer getVirtualSwitchTimeout() {
	return nominalSwitchTimeout;
}


public void retrieve() 
{
	Integer constraintValues[] = { getGroupID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setRouteID( (Integer) results[1] );
			setOperationalAddress( (String) results[2]);
			setNominalSwitchTimeout( (Integer) results[3]);
			setVirtualSwitchTimeout( (Integer) results[4]);
			
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}


public void setGroupID(Integer newGroupID) {
	groupID = newGroupID;
}

public void setRouteID(Integer newRouteID) {
	routeID = newRouteID;
}

public void setOperationalAddress(String newAddress) {
	operationalAddress = newAddress;
}

public void setNominalSwitchTimeout(Integer newTimeout) {
	nominalSwitchTimeout = newTimeout;
}

public void setVirtualSwitchTimeout(Integer newTimeout) {
	virtualSwitchTimeout = newTimeout;
}


public void update() 
{
	Object setValues[] =
	{ 
		getGroupID(),
		getRouteID(),
		getOperationalAddress(),
		getNominalSwitchTimeout(), 
		getVirtualSwitchTimeout()
	
	};
	
	Object constraintValues[] = { getGroupID() };
	
	try
	{
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}
}
