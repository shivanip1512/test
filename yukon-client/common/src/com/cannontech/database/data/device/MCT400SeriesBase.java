package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceMCT400Series;

/**
 * This type was created in VisualAge.
 */
public class MCT400SeriesBase extends MCTBase 
{
	private DeviceMCT400Series deviceMCT400Series = null;
	private boolean hasNewTOU = false;
	private boolean hasNewDisconnect = false;

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
	if(hasNewMCT400SeriesSettings())
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
	
	if(hasNewMCT400SeriesSettings())
		getDeviceMCT400Series().add();
		
}

/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	if(hasExistingMCT400SeriesSettings())
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
	
	if(hasExistingMCT400SeriesSettings())
		getDeviceMCT400Series().update();
	else if(hasNewTOU || hasNewDisconnect)
		getDeviceMCT400Series().add();
}

//this should all be temporary
//replace with a decent plan
private boolean hasExistingMCT400SeriesSettings()
{
	return (hasExistingTOU() || hasExistingDisconnect());
}

private boolean hasNewMCT400SeriesSettings()
{
	return ((!hasExistingMCT400SeriesSettings()) && (hasNewTOU || hasNewDisconnect));
}

public void setHasNewTOU(boolean usesTOU)
{
	hasNewTOU = usesTOU;
}

public void setHasNewDisconnect(boolean usesDisconn)
{
	hasNewDisconnect = usesDisconn;
}

public boolean hasExistingDisconnect()
{
	try
	{
		return DeviceMCT400Series.hasExistingDisconnectAddress(getDevice().getDeviceID());
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return false;
}

public boolean hasExistingTOU()
{
	try
	{
		return DeviceMCT400Series.hasExistingTOUSchedule(getDevice().getDeviceID());
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return false;
}

}
