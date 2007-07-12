package com.cannontech.database.db.device.lm;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */

public class LMProgramEnergyExchange extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer minNotifyTime = null;
	private String heading = null;
	private String messageHeader = null;
	private String messageFooter = null;	
	private String canceledMsg = null;
	private String stoppedEarlyMsg = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"MinNotifyTime", "Heading", "MessageHeader",
		"MessageFooter", "CanceledMsg", "StoppedEarlyMsg"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramEnergyExchange";
/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramEnergyExchange() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getMinNotifyTime(), 
					getHeading(), getMessageHeader(), getMessageFooter(),
					getCanceledMsg(), getStoppedEarlyMsg() };

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
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final LMEnergyExchangeCustomerList[] getAllCustomerList(Integer programDeviceID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	String sql = "select l.ProgramID, l.CustomerID, l.customerorder " +
				 "from " +
				 LMEnergyExchangeCustomerList.TABLE_NAME + " l " +
				 "where l.ProgramID = ? " +
				 "order by l.customerorder";

	try
	{		
		//conn = com.cannontech.database.PoolManager.getInstance().getConnection(
							//com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, programDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMEnergyExchangeCustomerList customer = new LMEnergyExchangeCustomerList();
				
				customer.setDeviceID( new Integer(rset.getInt("ProgramID")) );
				customer.setCustomerID( new Integer(rset.getInt("CustomerID")) );
				customer.setCustomerOrder( new Integer(rset.getInt("CustomerOrder")) );

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
		SqlUtils.close(rset, pstmt );
	}


	LMEnergyExchangeCustomerList retVal[] = new LMEnergyExchangeCustomerList[ tmpList.size() ];
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
		setCanceledMsg( (String) results[4] );
		setStoppedEarlyMsg( (String) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

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
					getCanceledMsg(), getStoppedEarlyMsg() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
