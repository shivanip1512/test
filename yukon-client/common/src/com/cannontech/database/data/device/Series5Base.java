/*
 * Created on Mar 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.device;


import com.cannontech.database.data.pao.DeviceClasses;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Series5Base extends IDLCBase {
	/**
	 * SmartTransmitted constructor comment.
	 */
	public Series5Base() {
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );	
	}


	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
	}


	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
	}
}
