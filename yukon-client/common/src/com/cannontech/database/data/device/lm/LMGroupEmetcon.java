package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroupEmetcon extends LMGroup implements IGroupRoute
{
	private com.cannontech.database.db.device.lm.LMGroupEmetcon lmGroupEmetcon = null;
/**
 * LMGroupEmetcon constructor comment.
 */
public LMGroupEmetcon() 
{
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_EMETCON_GROUP[0] );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getLmGroupEmetcon().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:19:57 PM)
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getLmGroupEmetconDefaults().add();
}

public void setRouteID( Integer rtID_ )
{
	getLmGroupEmetcon().setRouteID( rtID_ );
}
	

public Integer getRouteID()
{
	return getLmGroupEmetcon().getRouteID();
}


/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getLmGroupEmetcon().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 3:02:23 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {
	
	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupEmetcon
 */
public com.cannontech.database.db.device.lm.LMGroupEmetcon getLmGroupEmetcon() {
	if( lmGroupEmetcon == null )
	{
		lmGroupEmetcon = new com.cannontech.database.db.device.lm.LMGroupEmetcon();
	}
	
	return lmGroupEmetcon;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 4:14:06 PM)
 * @return com.cannontech.database.data.device.lm.LMGroupEmetcon
 */
public com.cannontech.database.db.device.lm.LMGroupEmetcon getLmGroupEmetconDefaults()
{

	getLmGroupEmetcon().setAddressUsage(new Character('G'));
	getLmGroupEmetcon().setGoldAddress(new Integer(0));
	getLmGroupEmetcon().setSilverAddress(new Integer(0));
	getLmGroupEmetcon().setRelayUsage(new Character('A'));

	return getLmGroupEmetcon();
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getLmGroupEmetcon().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLmGroupEmetcon().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getLmGroupEmetcon().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.LMGroupEmetcon
 */
public void setLmGroupEmetcon(com.cannontech.database.db.device.lm.LMGroupEmetcon newValue) {
	this.lmGroupEmetcon = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getLmGroupEmetcon().update();
}
}
