package com.cannontech.stars.dr.deviceReconfig.model;

import com.cannontech.common.constants.YukonListEntry;

public class DeviceReconfigDeviceType {

	private String name;
	private YukonListEntry yukonListEntry;
	
	public DeviceReconfigDeviceType(String name, YukonListEntry yukonListEntry) {
		
		this.name = name;
		this.yukonListEntry = yukonListEntry;
	}
	
	public String getName() {
		return name;
	}
	public YukonListEntry getYukonListEntry() {
		return yukonListEntry;
	}
}
