package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.port.PortRadioSettings;
 
public class LocalRadioPort extends LocalSharedPort {
	private PortRadioSettings portRadioSettings = null;
/**
 * LocalRadioPort constructor comment.
 */
public LocalRadioPort() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortRadioSettings().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getPortRadioSettings().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortRadioSettings
 */
public PortRadioSettings getPortRadioSettings() {
	if( portRadioSettings == null )
		portRadioSettings = new PortRadioSettings();
	return portRadioSettings;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getPortRadioSettings().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortRadioSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param portID java.lang.Integer
 */
public void setPortID( Integer portID) {
	super.setPortID( portID );
	getPortRadioSettings().setPortID( portID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortRadioSettings
 */
public void setPortRadioSettings(PortRadioSettings newValue) {
	this.portRadioSettings = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getPortRadioSettings().update();
}
}
