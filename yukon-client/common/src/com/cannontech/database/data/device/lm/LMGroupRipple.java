package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroupRipple extends LMGroup
{
	private com.cannontech.database.db.device.lm.LMGroupRipple lmGroupRipple = null;
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupRipple() {
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_RIPPLE_GROUP[0] );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLmGroupRipple().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:28:11 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getLmGroupRippleDefaults().add();
	}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getLmGroupRipple().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 3:03:14 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
 */
public com.cannontech.database.db.device.lm.LMGroupRipple getLmGroupRipple() {
	if( lmGroupRipple == null )
	{
		lmGroupRipple = new com.cannontech.database.db.device.lm.LMGroupRipple();
	}
	return lmGroupRipple;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 4:22:45 PM)
 * @return com.cannontech.database.data.device.lm.LMGroupVersacom
 */
public com.cannontech.database.db.device.lm.LMGroupRipple getLmGroupRippleDefaults()
{

	getLmGroupRipple().setShedTime(new Integer(450));
	getLmGroupRipple().setControl("0000000000 0000000000 0000000000 00000000000 00000000000");
	getLmGroupRipple().setRestore("0000000000 0000000000 0000000000 00000000000 00000000000");
	

	return getLmGroupRipple();
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getLmGroupRipple().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLmGroupRipple().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLmGroupRipple().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.LMGroupVersacom
 */
public void setLmGroupRipple(com.cannontech.database.db.device.lm.LMGroupRipple newValue) {
	this.lmGroupRipple = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLmGroupRipple().update();
}
}
