package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.*;
 
public class PortDialupModem extends DBPersistent {
	private String modemType = null;
	private String initializationString = null;
	private String prefixNumber = null;
	private String suffixNumber = null;
	
	private Integer portID = null;
/**
 * PortDialupModem constructor comment.
 */
public PortDialupModem() {
	super();
	initialize( null, null, null, null,null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortDialupModem(Integer portNumber) {
	super();
	initialize( portNumber, null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param modemType java.lang.String
 * @param initializationString java.lang.String
 * @param prefixNumber java.lang.String
 * @param suffixNumber java.lang.String
 */
public PortDialupModem( Integer portNumber, String modemType, String initializationString, String prefixNumber, String suffixNumber) {
	super();
	initialize( portNumber, modemType, initializationString, prefixNumber, suffixNumber);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object values[] = { getPortID(),getModemType(), getInitializationString(), getPrefixNumber(), getSuffixNumber() };
	add( "PortDialupModem", values );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete( "PortDialupModem", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInitializationString() {
	return initializationString;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getModemType() {
	return modemType;
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
 * @return java.lang.String
 */
public String getPrefixNumber() {
	return prefixNumber;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSuffixNumber() {
	return suffixNumber;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param initializationString java.lang.String
 * @param prefixNumber java.lang.String
 * @param suffixNumber java.lang.String
 */
public void initialize( Integer portID, String modemType, String initializationString, String prefixNumber, String suffixNumber ) {

	setPortID( portID );
	setModemType( modemType );
	setInitializationString( initializationString );
	setPrefixNumber( prefixNumber );
	setSuffixNumber( suffixNumber );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	
	String columnNames[] = { "ModemType", "InitializationString", "PrefixNumber", "SuffixNumber" };
	String constraintColumnNames[] = { "PortID" };
	Object constraintColumnValues[] = { getPortID() };
	
	Object result[] =  retrieve( columnNames, "PortDialupModem", constraintColumnNames, constraintColumnValues );
	
	if( result.length == columnNames.length )
	{
		setModemType( (String) result[0] );
		setInitializationString( (String) result[1] );
		setPrefixNumber( (String) result[2] );
		setSuffixNumber( (String) result[3] );
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setInitializationString(String newValue) {
	this.initializationString = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setModemType(String newValue) {
	this.modemType = newValue;
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
 * @param newValue java.lang.String
 */
public void setPrefixNumber(String newValue) {
	this.prefixNumber = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setSuffixNumber(String newValue) {
	this.suffixNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	
	String setColumnNames[] = { "ModemType", "InitializationString", "PrefixNumber", "SuffixNumber" };
	Object setColumnValues[]= { getModemType(), getInitializationString(), getPrefixNumber(), getSuffixNumber() };

	String constraintColumnNames[] = { "PortID" };
	Object constraintColumnValues[] = { getPortID() };
	
	update( "PortDialupModem", setColumnNames, setColumnValues, constraintColumnNames, constraintColumnValues);
}
}
