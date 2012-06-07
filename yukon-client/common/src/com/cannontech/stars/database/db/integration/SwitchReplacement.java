package com.cannontech.stars.database.db.integration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class SwitchReplacement extends DBPersistent {

    private Integer replacementID = null; 
    private String serialNumber = null;
    private String woType = "";
    private String deviceType = "";
    private String userName = "";

    public static final String CONSTRAINT_COLUMNS[] = { "ReplacementID" };

    public static final String SETTER_COLUMNS[] = { "ReplacementID", "SerialNumber", "WOType", "DeviceType", "UserName"};

    public static final String TABLE_NAME = "SwitchReplacement";


public SwitchReplacement() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getReplacementID(), getSerialNumber(), getWOType(), getDeviceType(), getUserName()};

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
        setUserName( (String) results[4] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getReplacementID(), getSerialNumber(), getWOType(), getDeviceType(), getUserName()}; 
    		
	Object constraintValues[] = { getReplacementID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static List<SwitchReplacement> getAllSwitchReplacements()
{
    List<SwitchReplacement> changes = new ArrayList<SwitchReplacement>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
            	SwitchReplacement currentEntry = new SwitchReplacement();
            	if (stmt.getRow(i)[0] != null)
            		currentEntry.setReplacementID( new Integer(stmt.getRow(i)[0].toString()));
            	if (stmt.getRow(i)[1] != null)
            		currentEntry.setSerialNumber( stmt.getRow(i)[1].toString());
            	if (stmt.getRow(i)[2] != null)
            		currentEntry.setWOType( stmt.getRow(i)[2].toString());
            	if (stmt.getRow(i)[3] != null)
            		currentEntry.setDeviceType( stmt.getRow(i)[3].toString());
            	if (stmt.getRow(i)[4] != null)
            		currentEntry.setUserName( stmt.getRow(i)[4].toString());
            	changes.add(currentEntry);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return changes;
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

public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}
}
