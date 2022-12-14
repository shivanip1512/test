package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.DBPersistent;
 
public class PortDialupModem extends DBPersistent {
	private String modemType = null;
	private String initializationString = "AT&F";
	private String prefixNumber = " ";
	private String suffixNumber = " ";
	
	private Integer portID = null;
/**
 * PortDialupModem constructor comment.
 */
public PortDialupModem() 
{
	super();
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
