/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.device.lm;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMGroupSA305 extends LMGroup implements IGroupRoute {

	private com.cannontech.database.db.device.lm.LMGroupSA305 lmGroupSA305 = null;

	/**
	 * LMGroupSA305 constructor comment.
	 */
	
	public LMGroupSA305() 
	{
		super();
	
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_SA305_GROUP[0] );
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		super.add();
		getLMGroupSA305().add();
	}
		
		
	public void addPartial() throws java.sql.SQLException
	{
		super.addPartial();
		getLMGroupSA305().add();
	}
	/**
	* delete method comment.
	*/
	
	public void delete() throws java.sql.SQLException 
	{
		getLMGroupSA305().delete();
		super.delete();
	}
	
	public void setRouteID( Integer rtID_ )
	{
		getLMGroupSA305().setRouteID( rtID_ );
	}
	
	public Integer getRouteID()
	{
		return getLMGroupSA305().getRouteID();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
	 */
	public com.cannontech.database.db.device.lm.LMGroupSA305 getLMGroupSA305() 
	{
		if( lmGroupSA305 == null )
			lmGroupSA305 = new com.cannontech.database.db.device.lm.LMGroupSA305();

		return lmGroupSA305;
	}
	
	/**
	 * retrieve method comment.
	 */
	
	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getLMGroupSA305().retrieve();
	
	}
	/**
	 * Insert the method's description here.
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
	
		getLMGroupSA305().setDbConnection(conn);
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getLMGroupSA305().setGroupID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue com.cannontech.database.db.device.lm.LMGroupSA305
	 */
	public void setLMGroupSA305(com.cannontech.database.db.device.lm.LMGroupSA305 newValue) 
	{
		this.lmGroupSA305 = newValue;
	}

	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		super.update();
		getLMGroupSA305().update();
	}

}
