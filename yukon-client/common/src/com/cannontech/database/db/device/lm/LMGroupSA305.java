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
public class LMGroupSA305 extends com.cannontech.database.db.DBPersistent 
{
	private Integer groupID;
	private Integer routeID;
	private String addressUsage;
	private Integer utilityAddress;
	private Integer groupAddress = new Integer(-1);
	private Integer divisionAddress = new Integer(-1);
	private Integer substationAddress = new Integer(-1);
	private String individualAddress;
	private Integer rateFamily = new Integer(-1);
	private Integer rateMember = new Integer(-1);
	private Integer rateHierarchy = new Integer(-1);
	private String loadNumber;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"GROUPID", "ROUTEID", "ADDRESSUSAGE", "UTILITYADDRESS", 
		"GROUPADDRESS", "DIVISIONADDRESS", "SUBSTATIONADDRESS",
		"INDIVIDUALADDRESS", "RATEFAMILY", "RATEMEMBER", "RATEHIERARCHY",
		"LOADNUMBER" 
		
	};

	public static final String CONSTRAINT_COLUMNS[] = { "groupID" };

	public static final String TABLE_NAME = "LMGroupSA305";

/**
 * Baseline constructor comment.
 */
public LMGroupSA305() {
	super();
}
/**
 * Baseline constructor comment.
 */
public LMGroupSA305(Integer grID, Integer rID) {
	super();
	groupID = grID;
	routeID = rID;
		
}


public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getGroupID(), getRouteID(), getAddressUsage(),
		getUtilityAddress(), getGroupAddress(), 
		getDivisionAddress(), getSubstationAddress(),
		getIndividualAddress(), getRateFamily(),
		getRateMember(), getRateHierarchy(),
		getLoadNumber()
		
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
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
	}


public Integer getGroupID() {
	return groupID;
}

public Integer getRouteID() {
	return routeID;
}

public String getAddressUsage() {
	return addressUsage;
}

public Integer getUtilityAddress() {
	return utilityAddress;
}

public Integer getGroupAddress() {
	return groupAddress;
}

public Integer getDivisionAddress() {
	return divisionAddress;
}

public Integer getSubstationAddress() {
	return substationAddress;
}

public String getIndividualAddress() {
	return individualAddress;
}

public Integer getRateFamily() {
	return rateFamily;
}

public Integer getRateMember() {
	return rateMember;
}

public Integer getRateHierarchy() {
	return rateHierarchy;
}

public String getLoadNumber() {
	return loadNumber;
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
			setAddressUsage( (String) results[2] );
			setUtilityAddress( (Integer) results[3] );
			setGroupAddress( (Integer) results[4] );
			setDivisionAddress( (Integer) results[5] );
			setSubstationAddress( (Integer) results[6] );
			setIndividualAddress( (String) results[7] );
			setRateFamily( (Integer) results[8] );
			setRateMember( (Integer) results[9] );
			setRateHierarchy( (Integer) results[10] );
			setLoadNumber( (String) results[11] );
			
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

public void setAddressUsage(String newUsage) {
	addressUsage = newUsage;
}

public void setUtilityAddress(Integer newAddress) {
	utilityAddress = newAddress;
}

public void setGroupAddress(Integer newAddress) {
	groupAddress = newAddress;
}

public void setDivisionAddress(Integer newAddress) {
	divisionAddress = newAddress;
}

public void setSubstationAddress(Integer newAddress) {
	substationAddress = newAddress;
}

public void setIndividualAddress(String newAddress) {
	individualAddress = newAddress;
}

public void setRateFamily(Integer newFamily) {
	rateFamily = newFamily;
}

public void setRateMember(Integer newMember) {
	rateMember = newMember;
}

public void setRateHierarchy(Integer newHierarchy) {
	rateHierarchy = newHierarchy;
}

public void setLoadNumber(String newNumber) {
	loadNumber = newNumber;
}

public void update() 
{
	Object setValues[] =
	{ 
		getGroupID(),
		getRouteID(),
		getAddressUsage(),
		getUtilityAddress(), 
		getGroupAddress(),
		getDivisionAddress(),
		getSubstationAddress(),
		getIndividualAddress(),
		getRateFamily(),
		getRateMember(),
		getRateHierarchy(),
		getLoadNumber()
		
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
