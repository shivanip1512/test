package com.cannontech.capcontrol.dao.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;

public class AreaFields implements PaoTemplatePart {
	private int voltReductionPointId = 0;

	public int getVoltReductionPointId() {
		return voltReductionPointId;
	}

	public void setVoltReductionPointId(int voltReductionPointId) {
		this.voltReductionPointId = voltReductionPointId;
	}
}
