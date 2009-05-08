package com.cannontech.web.capcontrol.models;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.cbc.CapBankDevice;

public class ViewableCapBank {
	
	private CapBankDevice capBankDevice = null;
	private LiteYukonPAObject controlDevice = null;
	private boolean twoWayCbc = false;
	private boolean device701x = false;
	
	public ViewableCapBank(){
		
	}

	public CapBankDevice getCapBankDevice() {
		return capBankDevice;
	}

	public void setCapBankDevice(CapBankDevice capBankDevice) {
		this.capBankDevice = capBankDevice;
	}

	public LiteYukonPAObject getControlDevice() {
		return controlDevice;
	}

	public void setControlDevice(LiteYukonPAObject controlDevice) {
		this.controlDevice = controlDevice;
	}

	public boolean isTwoWayCbc() {
		return twoWayCbc;
	}

	public void setTwoWayCbc(boolean twoWay) {
		this.twoWayCbc = twoWay;
	}

	public boolean isDevice701x() {
		return device701x;
	}

	public void setDevice701x(boolean device701x) {
		this.device701x = device701x;
	}
	
}
