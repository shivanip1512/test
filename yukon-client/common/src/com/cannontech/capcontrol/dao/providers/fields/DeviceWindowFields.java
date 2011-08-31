package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.device.DeviceWindowTypesEnum;
import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceWindowFields implements PaoTemplatePart {
	private DeviceWindowTypesEnum type = DeviceWindowTypesEnum.SCAN;
	private int windowOpen = 0;
	private int windowClose = 0;
	private int alternateOpen = 0;
	private int alternateClose = 0;
	
	public DeviceWindowTypesEnum getType() {
		return type;
	}
	
	public void setType(DeviceWindowTypesEnum type) {
		this.type = type;
	}

	public int getWindowOpen() {
		return windowOpen;
	}

	public void setWindowOpen(int windowOpen) {
		this.windowOpen = windowOpen;
	}

	public int getWindowClose() {
		return windowClose;
	}

	public void setWindowClose(int windowClose) {
		this.windowClose = windowClose;
	}

	public int getAlternateOpen() {
		return alternateOpen;
	}

	public void setAlternateOpen(int alternateOpen) {
		this.alternateOpen = alternateOpen;
	}

	public int getAlternateClose() {
		return alternateClose;
	}

	public void setAlternateClose(int alternateClose) {
		this.alternateClose = alternateClose;
	}
}
