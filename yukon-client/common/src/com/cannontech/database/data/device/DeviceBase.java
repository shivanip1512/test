package com.cannontech.database.data.device;

import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DynamicDeviceScanData;


/**
 * This type was created in VisualAge.
 */
public abstract class DeviceBase extends com.cannontech.database.data.pao.YukonPAObject implements com.cannontech.common.editor.EditorPanel
{
	private Device device = null;
	protected boolean isPartialDelete;
/**
 * DeviceBase constructor comment.
 */
public DeviceBase() {
	super();
	initialize();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getDevice().getDeviceID() == null )
		setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	super.add();
	getDevice().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 4:08:46 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	setDeviceID(getDevice().getDeviceID());
}
/**
 * This returns true is the device can rebroadcast (through repeater)
 * @return boolean
 */
public boolean allowRebroadcast() {

	return false;
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	DynamicDeviceScanData.deleteDynamicDeviceScanData(getDevice().getDeviceID());

	if (!isPartialDelete)
	{
		getDevice().delete();
		super.delete();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:27:08 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException 
{
	isPartialDelete = true;
	this.delete();
	isPartialDelete = false;
}
/**
 * This method was created in VisualAge.
 * @param obj com.cannontech.database.data.device.DeviceBase
 */
public boolean equals(Object obj) 
{
	if( obj instanceof DeviceBase )
		return getDevice().getDeviceID().equals( ((DeviceBase) obj).getDevice().getDeviceID() ) ;
	else
		return super.equals(obj);
	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.Device
 */
public Device getDevice() 
{
	if( device == null )
		device = new Device();
		
	return device;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasRoute(Integer deviceID) throws java.sql.SQLException 
{	
	return hasRoute(deviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasRoute(Integer deviceID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT DeviceID FROM " + com.cannontech.database.db.route.Route.TABLE_NAME + " WHERE DeviceID=" + deviceID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	//Set defaults
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getDevice().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getDevice().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
public void setDevice(Device newValue) {
	this.device = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setDeviceClass( String devClass )
{
	getYukonPAObject().setPaoClass( devClass );
}
 /**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setPAObjectID( deviceID );
	getDevice().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setDeviceType( String devType )
{
	getYukonPAObject().setType( devType );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setDisableFlag( Character ch )
{
	getYukonPAObject().setDisableFlag( ch );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setPAOName( String name )
{
	super.setPAOName( name );
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	getDevice().update();
}
}
