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
public class LMGroupSADigital extends LMGroup implements IGroupRoute { 
	
	private com.cannontech.database.db.device.lm.LMGroupSASimple lmGroupSASimple = null;

	/**
	 * LMGroupSASimple constructor comment.
	 */
	
	public LMGroupSADigital() 
	{
		super();
	
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_SADIGITAL_GROUP[0] );
	}

	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		super.add();
		getLMGroupSASimple().add();
	}
		
		
	public void addPartial() throws java.sql.SQLException
	{
		super.addPartial();
		getLMGroupSASimple().add();
	}
	/**
	* delete method comment.
	*/
	
	public void delete() throws java.sql.SQLException 
	{
		getLMGroupSASimple().delete();
		super.delete();
	}
	
	public void setRouteID( Integer rtID_ )
	{
		getLMGroupSASimple().setRouteID( rtID_ );
	}
	
	public Integer getRouteID()
	{
		return getLMGroupSASimple().getRouteID();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.device.lm.LMGroupVersacom
	 */
	public com.cannontech.database.db.device.lm.LMGroupSASimple getLMGroupSASimple() 
	{
		if( lmGroupSASimple == null )
			lmGroupSASimple = new com.cannontech.database.db.device.lm.LMGroupSASimple();

		return lmGroupSASimple;
	}
	
	/**
	 * retrieve method comment.
	 */
	
	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getLMGroupSASimple().retrieve();
	
	}
	/**
	 * Insert the method's description here.
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
	
		getLMGroupSASimple().setDbConnection(conn);
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getLMGroupSASimple().setGroupID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue com.cannontech.database.db.device.lm.LMGroupSASimple
	 */
	public void setLMGroupSASimple(com.cannontech.database.db.device.lm.LMGroupSASimple newValue) 
	{
		this.lmGroupSASimple = newValue;
	}

	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		super.update();
		getLMGroupSASimple().update();
	}

}
