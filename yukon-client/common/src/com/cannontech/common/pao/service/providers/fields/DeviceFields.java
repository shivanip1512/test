package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceFields implements PaoTemplatePart {
	private String alarmInhibit = "N";
	private String controlInhibit = "N";
	
	public String getAlarmInhibit() {
		return alarmInhibit;
	}
	
	public void setAlarmInhibit(String alarmInhibit) {
		this.alarmInhibit = alarmInhibit;
	}
	
	public String getControlInhibit() {
		return controlInhibit;
	}
	
	public void setControlInhibit(String controlInhibit) {
		this.controlInhibit = controlInhibit;
	}
}
