package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceDirectCommSettingsFields implements PaoTemplatePart {
	private int portId;

	public DeviceDirectCommSettingsFields(int portId) {
		this.portId = portId;
	}
	
	public int getPortId() {
		return portId;
	}
}
