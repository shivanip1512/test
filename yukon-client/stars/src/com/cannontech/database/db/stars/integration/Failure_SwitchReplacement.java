package com.cannontech.database.db.stars.integration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class Failure_SwitchReplacement extends DBPersistent {

    private Integer replacementID = null; 
    private String serialNumber = null;
    private String woType = "";
    private String deviceType = "";
    private String errorMsg;
    private Date datetime;    

    public static final String CONSTRAINT_COLUMNS[] = { "ReplacementID" };

    public static final String SETTER_COLUMNS[] = { "ReplacementID", "SerialNumber", "WOType", "DeviceType"};

    public static final String TABLE_NAME = "SwitchReplacement";


public Failure_SwitchReplacement() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getReplacementID(), getSerialNumber(), getWOType(), getDeviceType()};

    add( TABLE_NAME, setValues );
}


public void setDbConnection(Connection newValue) {
	super.setDbConnection(newValue);
}
public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getReplacementID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getReplacementID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setReplacementID( (Integer) results[0] );
        setSerialNumber( (String) results[1] );
        setWOType( (String) results[2] );
        setDeviceType( (String) results[3] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getReplacementID(), getSerialNumber(), getWOType(), getDeviceType()}; 
    		
	Object constraintValues[] = { getReplacementID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public String getDeviceType() {
	return deviceType;
}

public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
}

public Integer getReplacementID() {
	return replacementID;
}

public void setReplacementID(Integer replacementID) {
	this.replacementID = replacementID;
}

public String getSerialNumber() {
	return serialNumber;
}

public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
}

public String getWOType() {
	return woType;
}

public void setWOType(String woType) {
	this.woType = woType;
}
public String getErrorMsg()
{
    return errorMsg;
}

public Date getDatetime()
{
    return datetime;
}

public void setErrorMsg(String newMsg)
{
    errorMsg = newMsg;
}

public void setDatetime(Date newDate)
{
    datetime = newDate;
}

}
