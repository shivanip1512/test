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
public class DeviceVerification extends com.cannontech.database.db.DBPersistent 
{
	private Integer receiverID;
	private Integer transmitterID;
	private String resendOnFail;
	

	public static final String SETTER_COLUMNS[] = 
	{ 
		"RECEIVERID", "TRANSMITTERID", "RESENDONFAIL"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "RECEIVERID" };

	public static final String TABLE_NAME = "DeviceVerification";
	

/**
 * DeviceVerification constructor comment.
 */
public DeviceVerification() {
	super();
}
/**
 * DeviceVerification constructor comment.
 */
public DeviceVerification(Integer recID, Integer transID, String rsendFail) {
	super();
	receiverID = recID;
	transmitterID = transID;
	resendOnFail = rsendFail;
		
}

public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getReceiverID(), getTransmitterID(),
		getResendOnFail()
	};

	add( TABLE_NAME, addValues );
}

public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getReceiverID());
}


public Integer getReceiverID() {
	return receiverID;
	}

public Integer getTransmitterID() {
	return transmitterID;
	}

public String getResendOnFail() {
	return resendOnFail;
}

public void retrieve() 
{
	Integer constraintValues[] = { getReceiverID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setTransmitterID( (Integer) results[1] );
			setResendOnFail( (String) results[2] );
		}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public void setReceiverID(Integer recID) {
	receiverID = recID;
}

public void setTransmitterID(Integer transID) {
	transmitterID = transID;
}

public void setResendOnFail(java.lang.String resFail) {
	resendOnFail = resFail;
}

public void update() 
{
	Object setValues[] =
	{ 
		getReceiverID(), getTransmitterID(),
		getResendOnFail()
	};
	
	Object constraintValues[] = { getReceiverID() };
	
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
