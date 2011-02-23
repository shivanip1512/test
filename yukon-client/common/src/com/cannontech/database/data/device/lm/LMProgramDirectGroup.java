package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectGroup extends LMGroup 
{
	private com.cannontech.database.db.device.lm.LMProgramDirectGroup lmProgramDirectGroup = null;

/**
 * LMGroupEmetcon constructor comment.
 */
public LMProgramDirectGroup() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLmProgramDirectGroup().add();
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	getLmProgramDirectGroup().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:29:53 PM)
 * @return com.cannontech.database.db.device.lm.LMProgramDirectGroup
 */
public com.cannontech.database.db.device.lm.LMProgramDirectGroup getLmProgramDirectGroup() 
{
	if( lmProgramDirectGroup == null )
		lmProgramDirectGroup = new com.cannontech.database.db.device.lm.LMProgramDirectGroup();
		
	return lmProgramDirectGroup;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getLmProgramDirectGroup().retrieve();

}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLmProgramDirectGroup().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLmProgramDirectGroup().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:29:53 PM)
 * @param newLmProgramDirectGroup com.cannontech.database.db.device.lm.LMProgramDirectGroup
 */
public void setLmProgramDirectGroup(com.cannontech.database.db.device.lm.LMProgramDirectGroup newLmProgramDirectGroup) {
	lmProgramDirectGroup = newLmProgramDirectGroup;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLmProgramDirectGroup().update();
}
}
