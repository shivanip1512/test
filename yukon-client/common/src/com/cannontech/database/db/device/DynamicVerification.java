/*
 * Created on Jul 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DynamicVerification extends com.cannontech.database.db.DBPersistent 
{
	private Integer logID;
	private Integer timestamp;
	private Integer receiverID;
	private Integer transmitterID;
	private String command;
	private String received;
	private Integer codeSequence;
	private String codeStatus;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"LOGID", "TIMESTAMP", "RECEIVERID", 
		"TRANSMITTERID", "COMMAND", "CODESEQUENCE", "CODESTATUS"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "LOGID" };

	public static final String TABLE_NAME = "DynamicVerification";
	
public DynamicVerification() {
	super();
}

public DynamicVerification(Integer lgID, Integer stamp, Integer recID, Integer transID, String com, Integer codeSeq, String stat) {
	super();
	logID = lgID;
	timestamp = stamp;
	receiverID = recID;
	transmitterID = transID;
	command = com;
	codeSequence = codeSeq;
	codeStatus = stat;
	
}

public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getLogID(), getTimestamp(),
		getReceiverID(), getTransmitterID(), 
		getCommand(), getCodeSequence(),
		getCodeStatus()
	};

	add( TABLE_NAME, addValues );
}

public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLogID());
}

public static synchronized Integer getNextLogID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(LogID)+1 FROM " + TABLE_NAME );	
				
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


public Integer getLogID() {
	return logID;
	}

public Integer getTimestamp() {
	return timestamp;
}

public Integer getReceiverID() {
	return receiverID;	
}

public Integer getTransmitterID() {
	return transmitterID;
}

public String getCommand() {
	return command;
}

public Integer getCodeSequence() {
	return codeSequence;
}

public String getCodeStatus() {
	return codeStatus;
}

public void retrieve() 
{
	Integer constraintValues[] = { getLogID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setTimestamp( (Integer) results[1] );
			setReceiverID( (Integer) results[2] );
			setTransmitterID( (Integer) results[3] );
			setCommand( (String) results[4] );
			setCodeSequence( (Integer) results[5]);
			setCodeStatus( (String) results[6] );
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setLogID(Integer lgID) {
	logID = lgID;
}

public void setTimestamp(Integer stamp) {
	timestamp = stamp;
}

public void setReceiverID(Integer recID) {
	receiverID = recID;
}

public void setTransmitterID(Integer transID) {
	transmitterID = transID;
}

public void setCommand(String com) {
	command = com;
}

public void setCodeSequence(Integer seq) {
	codeSequence = seq;
}

public void setCodeStatus(String stat) {
	codeStatus = stat;
}

public void update() 
{
	Object setValues[] =
	{ 
		getLogID(), getTimestamp(),
		getReceiverID(), getTransmitterID(), 
		getCommand(), getCodeSequence(),
		getCodeStatus()
	};
	
	Object constraintValues[] = { getLogID() };
	
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

