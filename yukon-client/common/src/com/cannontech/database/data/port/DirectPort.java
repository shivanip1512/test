package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.port.*;
 
public class DirectPort extends com.cannontech.database.data.pao.YukonPAObject implements com.cannontech.common.editor.EditorPanel
{
	private CommPort commPort = null;
	private PortSettings portSettings = null;
/**
 * DirectPort constructor comment.
 */
public DirectPort() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getCommPort().add();
	getPortSettings().add();
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getPortSettings().delete();
	getCommPort().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj com.cannontech.database.data.port.DirectPort
 */
public boolean equals(DirectPort obj) {
	return getCommPort().getPortID().equals( obj.getCommPort().getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj com.cannontech.database.data.port.DirectPort
 */
public boolean equals(Object obj) {

	if( obj instanceof DirectPort )
		return getCommPort().getPortID().equals( ((DirectPort)obj).getCommPort().getPortID() );
	else
		return super.equals(obj);
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.CommPort
 */
public CommPort getCommPort() 
{

	if( commPort == null )
		commPort = new CommPort();
		
	return commPort;
}
/**
 * This method was created in VisualAge.
 */
public String getPortName()
{
	return getYukonPAObject().getPaoName();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortSettings
 */
public PortSettings getPortSettings() {
	if( portSettings == null )
		portSettings = new PortSettings();
		
	return portSettings;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDevice(Integer portID) throws java.sql.SQLException 
{	
	return hasDevice(portID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDevice(Integer portID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT portID FROM " + com.cannontech.database.db.device.DeviceDirectCommSettings.TABLE_NAME + " WHERE portID=" + portID,
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
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getCommPort().retrieve();
	getPortSettings().retrieve();
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.CommPort
 */
public void setCommPort(CommPort newValue) {
	this.commPort = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getCommPort().setDbConnection(conn);
	getPortSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setDisableFlag( Character ch ) 
{
	getYukonPAObject().setDisableFlag( ch );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setPortID(Integer portID) 
{
	super.setPAObjectID( portID );

	getCommPort().setPortID(portID);
	getPortSettings().setPortID(portID);
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setPortName( String name ) 
{
	getYukonPAObject().setPaoName( name );
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortSettings
 */
public void setPortSettings(PortSettings newValue) {
	this.portSettings = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void setPortType( String value )
{
	getYukonPAObject().setType( value );
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	getCommPort().update();
	getPortSettings().update();
}
}
