package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class DeviceCbcFields implements PaoTemplatePart {
	private int serialNumber = 0;
	private int routeId = 0;
	
	public int getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public int getRouteId() {
		return routeId;
	}
	
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
}
