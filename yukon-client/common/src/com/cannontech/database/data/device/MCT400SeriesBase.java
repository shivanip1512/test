package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceMCT400Series;

/**
 * This type was created in VisualAge.
 */
public class MCT400SeriesBase extends MCTBase 
{
	private DeviceMCT400Series deviceMCT400Series = null;
	private boolean hasTOU = false;
	private boolean hasDisconnect = false;

/**
 * MCT constructor comment.
 */
public MCT400SeriesBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	if(hasMCT400SeriesSettings())
		getDeviceMCT400Series().add();
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:34:05 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	super.addPartial();
	
	if(hasMCT400SeriesSettings())
		getDeviceMCT400Series().add();
		
}

/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	if(hasMCT400SeriesSettings())
		getDeviceMCT400Series().delete();
	
	super.delete();
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:34:05 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException 
{
	super.deletePartial();
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceMeterGroup
 */
public DeviceMCT400Series getDeviceMCT400Series() {
	if( deviceMCT400Series == null )
		deviceMCT400Series = new DeviceMCT400Series();
		
	return deviceMCT400Series;
}

public void setDeviceMCT400Series( DeviceMCT400Series mct400Series )
{
	deviceMCT400Series = mct400Series;
}

/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	
	//if(hasMCT400SeriesSettings())
	getDeviceMCT400Series().retrieve();
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:34:05 AM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	
	getDeviceMCT400Series().setDbConnection(conn);
}

/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	
	getDeviceMCT400Series().setDeviceID(deviceID);
}

/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	
	if(hasMCT400SeriesSettings())
		getDeviceMCT400Series().add();
}

private boolean hasMCT400SeriesSettings()
{
	return (hasTOU || hasDisconnect);
}

public void setHasTOU(boolean usesTOU)
{
	hasTOU = usesTOU;
}

public void setHasDisconnect(boolean usesDisconn)
{
	hasDisconnect = usesDisconn;
}

public boolean hasDisconnect()
{
	hasDisconnect = false;
	
	try
	{
		hasDisconnect = DeviceMCT400Series.hasDisconnectAddress(getDevice().getDeviceID());
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return hasDisconnect;
}

public boolean hasTOU()
{
	hasTOU = false;
	
	try
	{
		hasTOU = DeviceMCT400Series.hasTOUSchedule(getDevice().getDeviceID());
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return hasTOU;
}

}
