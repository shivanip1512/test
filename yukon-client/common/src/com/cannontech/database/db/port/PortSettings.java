package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.database.db.DBPersistent;
 
public class PortSettings extends DBPersistent {
	private Integer portID = null;
	private Integer baudRate = null;
	private Integer cdWait = null;
	private String  lineSettings = null;
/**
 * SerialPortSettings constructor comment.
 */
public PortSettings() {
	super();
	initialize( null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortSettings( Integer portNumber ) {
	super();
	initialize( portNumber, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param physicalPort java.lang.String
 * @param baudRate java.lang.Integer
 * @param cdWait java.lang.Integer
 */
public PortSettings( Integer portNumber, Integer baudRate, Integer cdWait, String lineSettings ) {
	super();

	initialize( portNumber, baudRate, cdWait, lineSettings );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPortID(), getBaudRate(), getCdWait(), getLineSettings() };
	
	add( "PortSettings", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "PortSettings", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getBaudRate() {
	return baudRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getCdWait() {
	return cdWait;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getLineSettings() {
	return lineSettings;
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
 * @param portNumber java.lang.Integer
 * @param physicalPort java.lang.String
 * @param baudRate java.lang.Integer
 * @param cdWait java.lang.Integer
 */
public void initialize( Integer portID, Integer baudRate, Integer cdWait, String lineSettings ) {

	setPortID( portID );
	setBaudRate( baudRate);
	setCdWait( cdWait );
	setLineSettings( lineSettings );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "BaudRate", "CDWait", "LineSettings" };
	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	Object results[] = retrieve( selectColumns, "PortSettings", constraintColumns, constraintValues );
	if( results.length ==  selectColumns.length )
	{
		setBaudRate( (Integer) results[0] );
		setCdWait( (Integer) results[1]);
		setLineSettings( (String) results[2]);
	}
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setBaudRate(Integer newValue) {
	this.baudRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setCdWait(Integer newValue) {
	this.cdWait = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setLineSettings(String newValue) {
	this.lineSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = {  "BaudRate", "CDWait", "LineSettings" };
	Object setValues[] = {  getBaudRate(), getCdWait(), getLineSettings() };
	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update( "PortSettings", setColumns, setValues, constraintColumns, constraintValues );
}
}
