package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.database.YNBoolean;

public class DeviceFields implements PaoTemplatePart {
	private YNBoolean alarmInhibit = YNBoolean.NO;
	private YNBoolean controlInhibit = YNBoolean.NO;
	
	public YNBoolean getAlarmInhibit() {
		return alarmInhibit;
	}
	
	public void setAlarmInhibit(YNBoolean alarmInhibit) {
		this.alarmInhibit = alarmInhibit;
	}
	
	public YNBoolean getControlInhibit() {
		return controlInhibit;
	}
	
	public void setControlInhibit(YNBoolean controlInhibit) {
		this.controlInhibit = controlInhibit;
	}
}
