package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.database.db.DBPersistent;
 
public class PortTiming extends DBPersistent {
	
	private Integer preTxWait = null;
	private Integer rtsToTxWait = null;
	private Integer postTxWait = null;
	private Integer receiveDataWait = null;
	private Integer extraTimeOut = null;
	private Integer portID = null;
	private Integer postCommWait = null;
/**
 * SerialPortDelays constructor comment.
 */
public PortTiming() {
	super();
	initialize( null, null, null, null, null, null, null);
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortTiming( Integer portNumber) {
	super();
	initialize( portNumber, null, null, null,null, null, null);
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param preTxWait java.lang.Integer
 * @param rtsToTxWait java.lang.Integer
 * @param postTxWait java.lang.Integer
 * @param receiveDataWait java.lang.Integer
 * @param postCommWait java.lang.Integer
 */
public PortTiming( Integer portNumber, Integer preTxWait, Integer rtsToTxWait, Integer postTxWait, Integer receiveDataWait, Integer extraTimeOut, Integer postCommWait) 
{
	super();
	initialize( portNumber, preTxWait, rtsToTxWait, postTxWait, receiveDataWait, extraTimeOut, postCommWait);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPortID(), getPreTxWait(), getRtsToTxWait(), getPostTxWait(), getReceiveDataWait(), getExtraTimeOut(), getPostCommWait() };
	
	add( "PortTiming", addValues );

}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete( "PortTiming", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getExtraTimeOut() {
	return extraTimeOut;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPortID() {
	return portID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPostTxWait() {
	return postTxWait;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPreTxWait() {
	return preTxWait;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getReceiveDataWait() {
	return receiveDataWait;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRtsToTxWait() {
	return rtsToTxWait;
}
/**
 * @return java.lang.Integer
 */
public Integer getPostCommWait() {
        return postCommWait;
}
/**
 * This method was created in VisualAge.
 * @param PortNumber java.lang.Integer
 * @param preTxWait java.lang.Integer
 * @param rtsToTxWait java.lang.Integer
 * @param postTxWait java.lang.Integer
 * @param receiveDataWait java.lang.Integer
 * @param extraTimeout java.lang.Integer
 * @param postCommWait java.lang.Integer
 */
public void initialize( Integer portID, Integer preTxWait, Integer rtsToTxWait, Integer postTxWait, Integer receiveDataWait, Integer extraTimeOut, Integer postCommWait) {

	setPortID( portID );
	setPreTxWait( preTxWait );
	setRtsToTxWait( rtsToTxWait );
	setPostTxWait( postTxWait );
	setReceiveDataWait( receiveDataWait );
	setExtraTimeOut( extraTimeOut );
	setPostCommWait(postCommWait);
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "PreTxWait", "RTSToTxWait", "PostTxWait", "ReceiveDataWait", "ExtraTimeOut", "PostCommWait" };
	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	Object results[] = retrieve( selectColumns, "PortTiming", constraintColumns, constraintValues );
	if( results.length == selectColumns.length)
	{
		setPreTxWait( (Integer) results[0] );
		setRtsToTxWait( (Integer) results[1] );
		setPostTxWait( (Integer) results[2] );
		setReceiveDataWait( (Integer) results[3] );
		setExtraTimeOut( (Integer) results[4] );
		setPostCommWait( (Integer) results[5]);
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setExtraTimeOut(Integer newValue) {
	this.extraTimeOut = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPostTxWait(Integer newValue) {
	this.postTxWait = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPreTxWait(Integer newValue) {
	this.preTxWait = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setReceiveDataWait(Integer newValue) {
	this.receiveDataWait = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRtsToTxWait(Integer newValue) {
	this.rtsToTxWait = newValue;
}
/**
 * @param newValue java.lang.Integer
 */
public void setPostCommWait(Integer newValue) {
        this.postCommWait = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "PreTxWait", "RTSToTxWait", "PostTxWait", "ReceiveDataWait", "ExtraTimeOut", "PostCommWait" };
	Object setValues[] = { getPreTxWait(), getRtsToTxWait(), getPostTxWait(), getReceiveDataWait(), getExtraTimeOut(), getPostCommWait() };

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update( "PortTiming", setColumns, setValues, constraintColumns, constraintValues );
}
}
