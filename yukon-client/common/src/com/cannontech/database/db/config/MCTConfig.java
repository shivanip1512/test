/*
 * Created on Dec 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.config;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class MCTConfig extends com.cannontech.database.db.DBPersistent 
{
	private Integer configID;
	private String configName;
	private Integer configType;
	private String configMode;
	private Integer mctWire1;
	private Double ke1;
	private Integer mctWire2 = new Integer(-1);
	private Double ke2 = new Double(-1);
	private Integer mctWire3 = new Integer(-1);
	private Double ke3 = new Double(-1);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CONFIGID", "CONFIGNAME", "CONFIGTYPE", 
		"CONFIGMODE", "MCTWIRE1", "KE1", "MCTWIRE2",
		"KE2", "MCTWIRE3", "KE3" 
	};

	public static final String CONSTRAINT_COLUMNS[] = { "CONFIGID" };

	public static final String TABLE_NAME = "MCTConfig";
	
	public final static Integer NONVALUE = new Integer(-1);

/**
 * Baseline constructor comment.
 */
public MCTConfig() {
	super();
}
/**
 * Baseline constructor comment.
 */
public MCTConfig(Integer conID, String name, Integer type, String mode, Integer wire1, Double KE1, Integer wire2, Double KE2, Integer wire3, Double KE3) {
	super();
	configID = conID;
	configName = name;
	configType = type;
	configMode = mode;
	mctWire1 = wire1;
	ke1 = KE1;
	mctWire2 = wire2;
	ke2 = KE2;
	mctWire3 = wire3;
	ke3 = KE3;
	
}


public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getConfigID(), getConfigName(),
		getConfigType(), getConfigMode(), 
		getMCTWire1(), getKe1(), 
		getMCTWire2(), getKe2(), 
		getMCTWire3(), getKe3()
	};

	add( TABLE_NAME, addValues );
}


public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getConfigID());
}

   
public static synchronized Integer getNextConfigID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(ConfigID)+1 FROM " + TABLE_NAME );	
				
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


public java.lang.Integer getConfigID() {
	return configID;
	}

public String getConfigName() {
	return configName;
}

public java.lang.Integer getConfigType() {
	return configType;
}

public java.lang.String getConfigMode() {
	return configMode;
}

public java.lang.Integer getMCTWire1() {
	return mctWire1;
}

public java.lang.Double getKe1() {
	return ke1;
}

public java.lang.Integer getMCTWire2() {
	return mctWire2;
}

public java.lang.Double getKe2() {
	return ke2;
}

public java.lang.Integer getMCTWire3() {
	return mctWire3;
}

public java.lang.Double getKe3() {
	return ke3;
}

public void retrieve() 
{
	Integer constraintValues[] = { getConfigID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setConfigName( (String) results[1] );
			setConfigType( (Integer) results[2] );
			setConfigMode( (String) results[3] );
			setMCTWire1( (Integer) results[4] );
			setKe1( (Double) results[5] );
			setMCTWire2( (Integer) results[6] );
			setKe2( (Double) results[7] );
			setMCTWire3( (Integer) results[8] );
			setKe3( (Double) results[9] );
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setConfigID(java.lang.Integer conID) {
	configID = conID;
}

public void setConfigName(java.lang.String newName) {
	configName = newName;
}

public void setConfigType(java.lang.Integer type) {
	configType = type;
}

public void setConfigMode(java.lang.String mode) {
	configMode = mode;
}

public void setMCTWire1(java.lang.Integer wire1) {
	mctWire1 = wire1;
}

public void setKe1(java.lang.Double newKE1Val) {
	ke1 = newKE1Val;
}

public void setMCTWire2(java.lang.Integer wire2) {
	mctWire2 = wire2;
}

public void setKe2(java.lang.Double newKE2Val) {
	ke2 = newKE2Val;
}

public void setMCTWire3(java.lang.Integer wire3) {
	mctWire3 = wire3;
}

public void setKe3(java.lang.Double newKE3Val) {
	ke3 = newKE3Val;
}

public void update() 
{
	Object setValues[] =
	{ 
		getConfigID(), getConfigName(),
		getConfigType(), getConfigMode(), 
		getMCTWire1(), getKe1(), 
		getMCTWire2(), getKe2(), 
		getMCTWire3(), getKe3()
	};
	
	Object constraintValues[] = { getConfigID() };
	
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
