package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;

public abstract class LMGroup extends DeviceBase
{
	private com.cannontech.database.db.device.lm.LMGroup lmGroup = null;
/**
 * LMGroupEmetcon constructor comment.
 */
public LMGroup() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLmGroup().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:29:05 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getLmGroup().add();
	
	}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete("DynamicLMControlHistory", "PAObjectID", getDevice().getDeviceID());
	
	delete("DynamicLMGroup", "DeviceID", getDevice().getDeviceID());

	delete(
		com.cannontech.database.db.macro.GenericMacro.TABLE_NAME,
		"childID", 
		getDevice().getDeviceID());

	LMProgramDirectGroup.deleteGroupsFromProgram(
		getDevice().getDeviceID(), getDbConnection() );
	
	getLmGroup().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 3:04:06 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {
	
	super.deletePartial();
	
	}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:16:43 PM)
 * @return com.cannontech.database.db.device.lm.LMGroup
 */
public com.cannontech.database.db.device.lm.LMGroup getLmGroup() 
{
	if( lmGroup == null )
		lmGroup = new com.cannontech.database.db.device.lm.LMGroup();
		
	return lmGroup;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getLmGroup().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLmGroup().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLmGroup().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:16:43 PM)
 * @param newLmGroup com.cannontech.database.db.device.lm.LMGroup
 */
public void setLmGroup(com.cannontech.database.db.device.lm.LMGroup newLmGroup) {
	lmGroup = newLmGroup;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLmGroup().update();
}
}
