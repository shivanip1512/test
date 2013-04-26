package com.cannontech.database.db.device;

import java.util.Date;

import com.cannontech.database.SqlUtils;

public class DynamicVerification extends com.cannontech.database.db.DBPersistent 
{
	private Long logID;
	private Date timeArrival;
	private Integer receiverID;
	private Integer transmitterID;
	private String command;
	private String code;
	private Character received;
	private Integer codeSequence;
	private String codeStatus;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"LOGID", "TIMEARRIVAL", "RECEIVERID", 
		"TRANSMITTERID", "COMMAND", "CODE", "CODESEQUENCE", "RECEIVED", "CODESTATUS"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "LOGID" };

	public static final String TABLE_NAME = "DynamicVerification";
	
public DynamicVerification() {
	super();
}

public DynamicVerification(Long lgID, Date date_, Integer recID, Integer transID, String command_, String code_, Integer codeSeq, Character received_, String stat) {
	super();
	logID = lgID;
	timeArrival = date_;
	receiverID = recID;
	transmitterID = transID;
	command = command_;
	code = code_;
	codeSequence = codeSeq;
	received = received_;
	codeStatus = stat;
}

public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getLogID(), getTimeArrival(),
		getReceiverID(), getTransmitterID(), 
		getCommand(), getCode(), 
		getCodeSequence(), getReceived(), 
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
		SqlUtils.close(rset, stmt);
	}
	
	//strange, should not get here
	return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
}


public Long getLogID() {
	return logID;
	}

public Date getTimeArrival() {
	return timeArrival;
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
	Long constraintValues[] = { getLogID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setLogID( (Long) results[0]);
			setTimeArrival( (Date) results[1] );
			setReceiverID( (Integer) results[2] );
			setTransmitterID( (Integer) results[3] );
			setCommand( (String) results[4] );
			setCode( (String) results[5]);
			setCodeSequence( (Integer) results[6]);
			setReceived( (Character) results[7]);
			setCodeStatus( (String) results[8]);
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setLogID(Long lgID) {
	logID = lgID;
}

public void setTimeArrival(Date date_) {
	timeArrival = date_;
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
		getLogID(), getTimeArrival(),
		getReceiverID(), getTransmitterID(), 
		getCommand(), getCodeSequence(),
		getReceived(), getCodeStatus()
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
	/**
	 * @return
	 */
	public Character getReceived()
	{
		return received;
	}

	/**
	 * @param character
	 */
	public void setReceived(Character character)
	{
		received = character;
	}
	/**
	 * @return
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param string
	 */
	public void setCode(String string)
	{
		code = string;
	}
}