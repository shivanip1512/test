package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroupMCT extends LMGroup implements IGroupRoute
{
	private com.cannontech.database.db.device.lm.LMGroupMCT lmGroupMCT = null;


	/**
	 * LMGroupMCT constructor comment.
	 */
	public LMGroupMCT() {
		super();
	
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_MCT_GROUP[0] );
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException {
		super.add();
		getLmGroupMCT().add();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/20/2001 2:28:11 PM)
	 * @exception java.sql.SQLException The exception description.
	 */
	public void addPartial() throws java.sql.SQLException
	{
		super.addPartial();
		getLmGroupMCT().add();
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		getLmGroupMCT().delete();
		super.delete();
	}
	
	public void setRouteID( Integer rtID_ )
	{
		getLmGroupMCT().setRouteID( rtID_ );
	}
	

	public Integer getRouteID()
	{
		return getLmGroupMCT().getRouteID();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
	 */
	public com.cannontech.database.db.device.lm.LMGroupMCT getLmGroupMCT() 
	{
		if( lmGroupMCT == null )
			lmGroupMCT = new com.cannontech.database.db.device.lm.LMGroupMCT();

		return lmGroupMCT;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getLmGroupMCT().retrieve();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/00 3:32:03 PM)
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
	
		getLmGroupMCT().setDbConnection(conn);
	}
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getLmGroupMCT().setDeviceID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue com.cannontech.database.db.device.lm.LMGroupMCT
	 */
	public void setLmGroupMCT(com.cannontech.database.db.device.lm.LMGroupMCT newValue) 
	{
		this.lmGroupMCT = newValue;
	}

	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		super.update();
		getLmGroupMCT().update();
	}
}
