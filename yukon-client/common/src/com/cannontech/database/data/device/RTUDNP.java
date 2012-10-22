package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;

public class RTUDNP extends DNPBase {
	public RTUDNP()	{
		super();
        setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
	}
}
