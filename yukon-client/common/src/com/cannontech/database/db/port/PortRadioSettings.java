package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.database.db.*;
 
public class PortRadioSettings extends DBPersistent {
	
	private Integer rtsToTxWaitSameD = null;
	private Integer rtsToTxWaitDiffD = null;
	private Integer radioMasterTail = null;
	private Integer reverseRTS = null;
	private Integer portID = null;
/**
 * RadioPortSettings constructor comment.
 */
public PortRadioSettings() {
	super();
	initialize( null, null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortRadioSettings( Integer portNumber) {
	super();

	initialize( portNumber, null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param rtsToTxWaitSameD java.lang.Integer
 * @param rtsToTxWaitDiffD java.lang.Integer
 * @param radioMasterTail java.lang.Integer
 * @param reverseRTS java.lang.Integer
 */
public PortRadioSettings( Integer portNumber, Integer rtsToTxWaitSameD, Integer rtsToTxWaitDiffD, Integer radioMasterTail, Integer reverseRTS ) {
	super();
	initialize( portNumber, rtsToTxWaitSameD, rtsToTxWaitDiffD, radioMasterTail, reverseRTS );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object addValues[] = { getPortID(), getRtsToTxWaitSameD(), getRtsToTxWaitDiffD(), getRadioMasterTail(), getReverseRTS() };
	
	add( "PortRadioSettings", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "PortRadioSettings", "PortID", getPortID() );
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
public Integer getRadioMasterTail() {
	return radioMasterTail;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getReverseRTS() {
	return reverseRTS;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRtsToTxWaitDiffD() {
	return rtsToTxWaitDiffD;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRtsToTxWaitSameD() {
	return rtsToTxWaitSameD;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param rtsToTxWaitSameD java.lang.Integer
 * @param rtsToTxWaitDiffD java.lang.Integer
 * @param radioMasterTail java.lang.Integer
 * @param reverseRTS java.lang.Integer
 */
public void initialize( Integer portID, Integer rtsToTxWaitSameD, Integer rtsToTxWaitDiffD, Integer radioMasterTail, Integer reverseRTS ) {

	setPortID( portID );
	setRtsToTxWaitSameD( rtsToTxWaitSameD );
	setRtsToTxWaitDiffD( rtsToTxWaitDiffD );
	setRadioMasterTail( radioMasterTail );
	setReverseRTS( reverseRTS );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	
	String selectColumns[] = { "RTSToTxWaitSameD", "RTSToTxWaitDiffD", "RadioMasterTail", "ReverseRTS" };

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	Object results[] = retrieve( selectColumns, "PortRadioSettings", constraintColumns, constraintValues );
	
	if( results.length == selectColumns.length )
	{
		setRtsToTxWaitSameD((Integer) results[0] );
		setRtsToTxWaitDiffD( (Integer) results[1] );
		setRadioMasterTail( (Integer) results[2] );
		setReverseRTS( (Integer) results[3] );
	}
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
public void setRadioMasterTail(Integer newValue) {
	this.radioMasterTail = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setReverseRTS(Integer newValue) {
	this.reverseRTS = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRtsToTxWaitDiffD(Integer newValue) {
	this.rtsToTxWaitDiffD = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRtsToTxWaitSameD(Integer newValue) {
	this.rtsToTxWaitSameD = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	
	String setColumns[] = { "RTSToTxWaitSameD", "RTSToTxWaitDiffD", "RadioMasterTail", "ReverseRTS" };
	Object setValues[] = { getRtsToTxWaitSameD(), getRtsToTxWaitDiffD(), getRadioMasterTail(), getReverseRTS()};

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update( "PortRadioSettings", setColumns, setValues, constraintColumns, constraintValues );
}
}
