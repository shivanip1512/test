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
public class LMGroupSA205105 extends LMGroup implements IGroupRoute {

	private com.cannontech.database.db.device.lm.LMGroupSA205105 lmGroupSA205105 = null;

	/**
	 * LMGroupSA205105 constructor comment.
	 */
	
	public LMGroupSA205105() 
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
		getLMGroupSA205105().add();
	}
		
	public void addPartial() throws java.sql.SQLException
	{
		super.addPartial();
		getLMGroupSA205105().add();
	}
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		getLMGroupSA205105().delete();
		super.delete();
	}
	
	public void setRouteID( Integer rtID_ )
	{
		getLMGroupSA205105().setRouteID( rtID_ );
	}
	
	public Integer getRouteID()
	{
		return getLMGroupSA205105().getRouteID();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
	 */
	
	public com.cannontech.database.db.device.lm.LMGroupSA205105 getLMGroupSA205105() 
	{
		if( lmGroupSA205105 == null )
			lmGroupSA205105 = new com.cannontech.database.db.device.lm.LMGroupSA205105();
		return lmGroupSA205105;
	}

	/**
	 * retrieve method comment.
	 */

	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getLMGroupSA205105().retrieve();

	}
	/**
	 * Insert the method's description here.
	 * @param conn java.sql.Connection
	 */

	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);

		getLMGroupSA205105().setDbConnection(conn);
	}

	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getLMGroupSA205105().setGroupID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue com.cannontech.database.db.device.lm.LMGroupSA205105
	 */
	public void setLMGroupSA205105(com.cannontech.database.db.device.lm.LMGroupSA205105 newValue) 
	{
		this.lmGroupSA205105 = newValue;
	}
		/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		super.update();
		getLMGroupSA205105().update();
	}
}
