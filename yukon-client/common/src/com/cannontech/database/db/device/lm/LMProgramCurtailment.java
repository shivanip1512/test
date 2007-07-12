package com.cannontech.database.db.device.lm;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.customer.CICustomerBase;


/**
 * This type was created in VisualAge.
 */

public class LMProgramCurtailment extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer minNotifyTime = null;
	private String heading = null;
	private String messageHeader = null;
	private String messageFooter = null;	
	private Integer ackTimeLimit = null;
	private String canceledMsg = null;
	private String stoppedEarlyMsg = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"MinNotifyTime", "Heading", "MessageHeader",
		"MessageFooter", "AckTimeLimit", "CanceledMsg",
		"StoppedEarlyMsg"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramCurtailment";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramCurtailment() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getMinNotifyTime(), 
					getHeading(), getMessageHeader(), getMessageFooter(),
					getAckTimeLimit(), getCanceledMsg(), getStoppedEarlyMsg() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAckTimeLimit() {
	return ackTimeLimit;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
// If returnReqAck is true, then we return the RequireAck character from the
//   lmProgramCurtailCustomerList table by setting the device.currentState() 
//   field!!
public static final com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList[] getAllCustomerList(Integer programCurtDeviceID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	String sql = "select l.ProgramID, c.companyname, l.requireAck, l.customerorder, l.CustomerID " +
		 "from " + CICustomerBase.TABLE_NAME + " c, lmProgramCurtailCustomerList l " + 
		 "where l.ProgramID = ? " +
		 "and c.customerid = l.CustomerID order by l.customerorder";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, programCurtDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList customer = new com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList();
				
				customer.setDeviceID( new Integer(rset.getInt("ProgramID")) );
				customer.setCustomerName( rset.getString("companyname") );
				customer.getLmProgramCurtailCustomerList().setRequireAck( rset.getString("RequireAck") );
				customer.getLmProgramCurtailCustomerList().setCustomerOrder( new Integer(rset.getInt("CustomerOrder")) );
				customer.getLmProgramCurtailCustomerList().setCustomerID( new Integer(rset.getInt("CustomerID")) );

				tmpList.add( customer );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}


	com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList retVal[] = new com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.String getCanceledMsg() {
	return canceledMsg;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.String getHeading() {
	return heading;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageFooter() {
	return messageFooter;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.String getMessageHeader() {
	return messageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.Integer getMinNotifyTime() {
	return minNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @return java.lang.String
 */
public java.lang.String getStoppedEarlyMsg() {
	return stoppedEarlyMsg;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setMinNotifyTime( (Integer) results[0] );
		setHeading( (String) results[1] );
		setMessageHeader( (String) results[2] );
		setMessageFooter( (String) results[3] );
		setAckTimeLimit( (Integer) results[4] );
		setCanceledMsg( (String) results[5] );
		setStoppedEarlyMsg( (String) results[6] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newAckTimeLimit java.lang.Integer
 */
public void setAckTimeLimit(java.lang.Integer newAckTimeLimit) {
	ackTimeLimit = newAckTimeLimit;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newCanceledMsg java.lang.String
 */
public void setCanceledMsg(java.lang.String newCanceledMsg) {
	canceledMsg = newCanceledMsg;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newHeading java.lang.String
 */
public void setHeading(java.lang.String newHeading) {
	heading = newHeading;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newMessageFooter java.lang.String
 */
public void setMessageFooter(java.lang.String newMessageFooter) {
	messageFooter = newMessageFooter;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newMessageHeader java.lang.String
 */
public void setMessageHeader(java.lang.String newMessageHeader) {
	messageHeader = newMessageHeader;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newMinNotifyTime java.lang.String
 */
public void setMinNotifyTime(java.lang.Integer newMinNotifyTime) {
	minNotifyTime = newMinNotifyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:40:46 PM)
 * @param newStoppedEarlyMsg java.lang.String
 */
public void setStoppedEarlyMsg(java.lang.String newStoppedEarlyMsg) {
	stoppedEarlyMsg = newStoppedEarlyMsg;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getMinNotifyTime(), 
					getHeading(), getMessageHeader(), getMessageFooter(),
					getAckTimeLimit(), getCanceledMsg(), getStoppedEarlyMsg() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
