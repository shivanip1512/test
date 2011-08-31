package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class SubstationFields implements PaoTemplatePart {
	private int voltReductionPointId = 0;
	private String mapLocationId = "0";
	
	public int getVoltReductionPointId() {
		return voltReductionPointId;
	}
	
	public void setVoltReductionPointId(int voltReductionPointId) {
		this.voltReductionPointId = voltReductionPointId;
	}
	
	public String getMapLocationId() {
		return mapLocationId;
	}
	
	public void setMapLocationId(String mapLocationId) {
		this.mapLocationId = mapLocationId;
	}
}
