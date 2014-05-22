package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.config.MCTConfigMapping;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;
/**
 * This type was created in VisualAge.
 */
public class MCTBase extends CarrierBase implements IDeviceMeterGroup 
{
	private DeviceMeterGroup deviceMeterGroup = null;
	private DeviceLoadProfile deviceLoadProfile = null;
	private boolean hasConfig = false;
	private MCTConfigMapping configMapping = null;
/**
 * MCT constructor comment.
 */
public MCTBase(PaoType paoType) {
	super(paoType);
}
/**
 * This method was created in VisualAge.
 */
@Override
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceMeterGroup().add();
	getDeviceLoadProfile().add();
	if(hasConfig)
		getConfigMapping().add();
		
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:41:18 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void addPartial() throws java.sql.SQLException 
{
	super.addPartial();
	//getDeviceMeterGroupDefaults().add();
	getDeviceMeterGroup().add();
	getDeviceLoadProfile().add();
	if(hasConfig)
		getConfigMapping().add();	
	
}
/**
 * This method was created in VisualAge.
 */
@Override
public void delete() throws java.sql.SQLException 
{
	getDeviceMeterGroup().delete();
	getDeviceLoadProfile().delete();
	if(hasConfig)
		getConfigMapping().delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:10:07 AM)
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void deletePartial() throws java.sql.SQLException 
{
	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceCarrierSettings
 */
public DeviceLoadProfile getDeviceLoadProfile() {
 	if( deviceLoadProfile == null )
 		deviceLoadProfile = new DeviceLoadProfile();
 		
	return deviceLoadProfile;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceMeterGroup
 */
@Override
public DeviceMeterGroup getDeviceMeterGroup() {
	if( deviceMeterGroup == null )
		deviceMeterGroup = new DeviceMeterGroup();
		
	return deviceMeterGroup;
}

public MCTConfigMapping getConfigMapping() {
	if( configMapping == null )
		configMapping = new MCTConfigMapping();
	return configMapping;
}

public void setConfigMapping( MCTConfigMapping newConfig) {
	configMapping = newConfig;
}

public void setConfigMapping( Integer conID, Integer mctID) {
	getConfigMapping().setconfigID(conID);
	getConfigMapping().setmctID(mctID);
}

@Override
public void setDeviceMeterGroup( DeviceMeterGroup dvMtrGrp_ )
{
	deviceMeterGroup = dvMtrGrp_;
}

public void setDeviceLoadProfile(DeviceLoadProfile deviceLoadProfile) {
    this.deviceLoadProfile = deviceLoadProfile;
}

/**
 * This method was created in VisualAge.
 */
@Override
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getDeviceMeterGroup().retrieve();
	getDeviceLoadProfile().retrieve();
	if(hasConfig)
		getConfigMapping().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
@Override
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getDeviceLoadProfile().setDbConnection(conn);
	getDeviceMeterGroup().setDbConnection(conn);
	if(hasConfig)
		getConfigMapping().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
@Override
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceMeterGroup().setDeviceID(deviceID);
	getDeviceLoadProfile().setDeviceID(deviceID);

}

public void setHasConfig(boolean yesno)
{
	hasConfig = yesno;
}

/**
 * This method was created in VisualAge.
 */
@Override
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceMeterGroup().update();
	getDeviceLoadProfile().update();
	if(hasConfig)
		getConfigMapping().add();
}

public boolean hasMappedConfig()
{
	hasConfig = false;
	
	try
	{
		hasConfig = MCTConfigMapping.hasConfig(getDevice().getDeviceID());
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
	
	return hasConfig;
}

public Integer getConfigID()
{
	Integer id = new Integer(-1);
	
	if(hasConfig)
	{
		java.sql.Connection conn = null;
		try
		{
	
			conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
			id = MCTConfigMapping.getTheConfigID(getDevice().getDeviceID(), conn);
			
		}
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
		}	
		finally{
			SqlUtils.close(conn );
		}
	}
	else
		id = configMapping.getConfigID();
	
	return id;
}

}
