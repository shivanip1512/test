/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.DBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceMCT400Series extends DBPersistent {
	
	private Integer deviceID;
	private Integer disconnectAddress = null;
	private Integer touScheduleID = new Integer(0);
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"DEVICEID", "DISCONNECTADDRESS", "TOUSCHEDULEID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "DeviceMCT400Series";

/**
 * DeviceMCT400Series constructor comment.
 */
public DeviceMCT400Series() {
	super();
}
/**
 * DeviceMCT400Series constructor comment.
 */
public DeviceMCT400Series(Integer disconn, Integer touID) 
{
	super();
	disconnectAddress = disconn;
	touScheduleID = touID;
}

public DeviceMCT400Series(Integer scheduleID, Integer disconn, Integer touID) 
{
	super();
	deviceID = scheduleID;
	disconnectAddress = disconn;
	touScheduleID = touID;
}

public void add() throws java.sql.SQLException {
	Object addValues[] = { 
		getDeviceID(), getDisconnectAddress(), getTOUScheduleID()
	};

	add( TABLE_NAME, addValues );
}

public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID());
}

public Integer getTOUScheduleID() {
	return touScheduleID;
}

public Integer getDisconnectAddress() {
	return disconnectAddress;
}

public Integer getDeviceID() {
	return deviceID;
}

public void retrieve() {
	Integer constraintValues[] = { getDeviceID() };	
	
	try {
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length ) {
		    setDisconnectAddress((Integer) results[1]);
			setTOUScheduleID((Integer) results[2]);
		}
		
	}
	catch (Exception e)	{
		CTILogger.error( e.getMessage(), e );
	}
}

public void setTOUScheduleID(Integer newTOUScheduleID) {
	touScheduleID = newTOUScheduleID;
}

public void setDisconnectAddress(Integer newDisconn) {
    this.disconnectAddress = newDisconn;
}


public void setDeviceID(Integer newDeviceID) {
	deviceID = newDeviceID;
}

public void update() {
	Object setValues[] = { 
		getDeviceID(), getDisconnectAddress(), getTOUScheduleID()
	};
	
	Object constraintValues[] = { getDeviceID() };
	
	try {
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	} catch (Exception e) {
		CTILogger.error( e.getMessage(), e );
	}
}

/**
 * Returns true if the DeviceMCT400Series table has a row returned for mctID, else false.
 * @param mctID
 * @return
 */
public final static boolean hasExistingDisconnectAddress(Integer mctID) {
    
    JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
    SqlStatementBuilder sql = new SqlStatementBuilder();
    sql.append("SELECT DisconnectAddress FROM DeviceMCT400Series ");
    sql.append("WHERE DeviceID = ").appendArgument(mctID);
    try {
        int address = template.queryForObject(sql.getSql(), sql.getArguments(), Integer.class);
        return true;
    } catch (EmptyResultDataAccessException e) {
        // if no results, then it doesn't exist
        return false;
    }
}

}