package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceMeterGroup;

/**
 * @author rneuharth
 *
 * This interface if for DBPersistant objects that have a
 * DeviceMeterGroup instance.
 */
public interface IDeviceMeterGroup 
{

	DeviceMeterGroup getDeviceMeterGroup();
}
