package com.cannontech.database.data.command;

import java.util.ArrayList;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 10:50:03 AM)
 * @author: 
 */
public class DeviceTypeCommand extends DBPersistent implements CTIDbChange
{
	private com.cannontech.database.db.command.DeviceTypeCommand deviceTypeCommand;
	
	private com.cannontech.database.db.command.Command command;
	
/**
 * DeviceTypeCommand constructor comment.
 */
public DeviceTypeCommand() {
	super();
}

/**
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType)
{
	ArrayList<DBChangeMessage> list = new ArrayList<DBChangeMessage>(10);

	//add the basic change method
	list.add( new DBChangeMessage(
					getDeviceTypeCommand().getDeviceCommandID().intValue(),
					DBChangeMessage.CHANGE_DEVICETYPE_COMMAND_DB,
					DBChangeMessage.CAT_DEVICETYPE_COMMAND,
					DBChangeMessage.CAT_DEVICETYPE_COMMAND,
					dbChangeType) );
	 
	DBChangeMessage[] dbChange = new DBChangeMessage[list.size()];
	return list.toArray( dbChange );
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getDeviceTypeCommand().setDbConnection(getDbConnection());
	getDeviceTypeCommand().add();
	getDeviceTypeCommand().setDbConnection(null);
	getCommand().setCommandID(getDeviceTypeCommand().getCommandID());
	getCommand().setDbConnection(getDbConnection());
	getCommand().retrieve();
	getCommand().setDbConnection(null);
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{	
	getDeviceTypeCommand().setDbConnection(getDbConnection());
	getDeviceTypeCommand().delete();
	getDeviceTypeCommand().setDbConnection(null);
}
/**
 * @return com.cannontech.database.db.command.Command 
 */
public Command getCommand() {
	if( command == null )
		command = new Command();
		
	return command;
}
/**
 * @return com.cannontech.database.db.command.DeviceTypeCommand 
 */
public com.cannontech.database.db.command.DeviceTypeCommand getDeviceTypeCommand() {
	if( deviceTypeCommand == null )
		deviceTypeCommand = new com.cannontech.database.db.command.DeviceTypeCommand();
		
	return deviceTypeCommand;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getDeviceTypeCommand().setDbConnection(getDbConnection());
	getDeviceTypeCommand().retrieve();
	getCommand().setCommandID(getDeviceTypeCommand().getCommandID());
	getCommand().setDbConnection(getDbConnection());
	getCommand().retrieve();
}
/**
 * @param newCommands java.util.ArrayList
 */
public void setCommand(com.cannontech.database.db.command.Command newCommand) {
	command = newCommand;
	getDeviceTypeCommand().setCommandID(command.getCommandID());
}
/**
 * @param newDeviceTypeCommand com.cannontech.database.db.command.DeviceTypeCommand
 */
public void setDeviceTypeCommand(com.cannontech.database.db.command.DeviceTypeCommand newDeviceTypeCommand) {
	deviceTypeCommand = newDeviceTypeCommand;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	getDeviceTypeCommand().setDbConnection(getDbConnection());
	getDeviceTypeCommand().update();
	getDeviceTypeCommand().setDbConnection(null);
}
public Integer getDeviceCommandID()
{
	return getDeviceTypeCommand().getDeviceCommandID();
}
public Integer getCommandID()
{
	return getDeviceTypeCommand().getCommandID();
}
public Integer getCommandGroupID()
{
	return getDeviceTypeCommand().getCommandGroupID();
}
public String getDeviceType()
{
	return getDeviceTypeCommand().getDeviceType();
}
public Integer getDisplayOrder()
{
	return getDeviceTypeCommand().getDisplayOrder();
}
public Character getVisibleFlag()
{
	return getDeviceTypeCommand().getVisibleFlag();
}
public boolean isVisible()
{
	if( getVisibleFlag().charValue() == 'Y' )
		return true;
	return false;
}

public void setDeviceCommandID(Integer devID)
{
	getDeviceTypeCommand().setDeviceCommandID(devID);
}
public void setCommandID(Integer cmdID)
{
	getDeviceTypeCommand().setCommandID(cmdID);
	getCommand().setCommandID(cmdID);
}
public void setCommandGroupID(Integer cmdGrpID)
{
	getDeviceTypeCommand().setCommandGroupID(cmdGrpID);
}
public void setDeviceType(String devType)
{
	getDeviceTypeCommand().setDeviceType(devType);
}
public void setDisplayOrder(Integer dispOrder)
{
	getDeviceTypeCommand().setDisplayOrder(dispOrder);
}
public void setVisibleFlag(Character visible)
{
	getDeviceTypeCommand().setVisibleFlag(visible);
}

}
