package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.*;

public class LMGroupPoint extends LMGroup
{
	private com.cannontech.database.db.device.lm.LMGroupPoint lmGroupPoint = null;
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupPoint() {
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_POINT_GROUP[0] );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLMGroupPoint().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:28:11 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();
	getLMGroupPoint().add();
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	
	getLMGroupPoint().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupPoint
 */
public com.cannontech.database.db.device.lm.LMGroupPoint getLMGroupPoint() 
{
	if( lmGroupPoint == null )
		lmGroupPoint = new com.cannontech.database.db.device.lm.LMGroupPoint();

	return lmGroupPoint;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getLMGroupPoint().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLMGroupPoint().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLMGroupPoint().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.LMGroupPoint
 */
public void setLmGroupVersacom(com.cannontech.database.db.device.lm.LMGroupPoint newValue) 
{
	lmGroupPoint = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLMGroupPoint().update();
}
}
