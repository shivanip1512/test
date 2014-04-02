package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import org.apache.commons.lang3.Validate;

import com.cannontech.database.db.DBPersistent;
 
public class PortTerminalServer extends DBPersistent {
	
    public enum EncodingType {
        NONE(0),
        AES(1);

        private final int pos;
        
        EncodingType(int pos) {
            this.pos = pos;
        }
        
        public int getValue() {
            return pos;
        }
    }
    
	private String ipAddress = null;
	private Integer socketPortNumber = null;
	private Integer portID = null;
	private EncodingType encodingType = EncodingType.NONE;
	private String encodingKey = "";
	
/**
 * PortTerminalServer constructor comment.
 */
public PortTerminalServer() {
	super();
	initialize( null, null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortTerminalServer( Integer portNumber) {
	super();
	initialize( portNumber, null, null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param ipAddress java.lang.String
 * @param socketPortNumber java.lang.Integer
 */
public PortTerminalServer( Integer portNumber, String ipAddress, Integer socketPortNumber) {
	super();
	initialize( portNumber, ipAddress, socketPortNumber, null, null );
}

public PortTerminalServer( Integer portNumber, String ipAddress, Integer socketPortNumber, EncodingType type, String key) {
    super();
    initialize( portNumber, ipAddress, socketPortNumber, type, key );
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPortID(), getIpAddress(), getSocketPortNumber(), getEncodingKey(), getEncodingType().toString() };
	
	add( "PortTerminalServer", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete("PortTerminalServer", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getIpAddress() {
	return ipAddress;
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
public Integer getSocketPortNumber() {
	return socketPortNumber;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param ipAddress java.lang.String
 * @param socketPortNumber java.lang.Integer
 */
public void initialize( Integer portID, String ipAddress, Integer socketPortNumber, EncodingType type, String newKey ) {

	setPortID( portID );
	setIpAddress( ipAddress );
	setSocketPortNumber( socketPortNumber );
	
	if(type != null) {
	    setEncodingType(type);
	}
	
	if (newKey != null) {
	    setEncodingKey(newKey);
	}
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	
	String selectColumns[] = { "IPAddress", "SocketPortNumber", "EncodingType", "EncodingKey" };
	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	Object results[] = retrieve( selectColumns, "PortTerminalServer", constraintColumns, constraintValues );
	if( results.length == selectColumns.length )
	{
		setIpAddress( (String) results[0] );
		setSocketPortNumber( (Integer) results[1] );
		setEncodingType(EncodingType.valueOf((String) results[2]));
		setEncodingKey((String)results[3]);
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setIpAddress(String newValue) {
	this.ipAddress = newValue;
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
public void setSocketPortNumber(Integer newValue) {
	this.socketPortNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	
	String setColumns[] = { "IPAddress", "SocketPortNumber", "EncodingType", "EncodingKey" };
	Object setValues[] = { getIpAddress(), getSocketPortNumber(), getEncodingType().toString(), getEncodingKey()  };

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update( "PortTerminalServer", setColumns, setValues, constraintColumns, constraintValues );
}
public EncodingType getEncodingType() {
    return encodingType;
}
public void setEncodingType(EncodingType type) {
    Validate.notNull(type);
    this.encodingType = type;
}
public String getEncodingKey() {
    return encodingKey;
}
public void setEncodingKey(String key) {
    Validate.notNull(key);
    this.encodingKey = key;
}
}
