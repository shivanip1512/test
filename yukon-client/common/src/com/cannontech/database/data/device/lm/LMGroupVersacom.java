package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroupVersacom extends LMGroup
{
	private com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom = null;
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupVersacom() {
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_VERSACOM_GROUP[0] );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLmGroupVersacom().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:28:11 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();
	getLmGroupVersacom().add();
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getLmGroupVersacom().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
 */
public com.cannontech.database.db.device.lm.LMGroupVersacom getLmGroupVersacom() {
	if( lmGroupVersacom == null )
	{
		lmGroupVersacom = new com.cannontech.database.db.device.lm.LMGroupVersacom();
	}
	return lmGroupVersacom;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getLmGroupVersacom().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLmGroupVersacom().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLmGroupVersacom().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.LMGroupVersacom
 */
public void setLmGroupVersacom(com.cannontech.database.db.device.lm.LMGroupVersacom newValue) {
	this.lmGroupVersacom = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLmGroupVersacom().update();
}
}
