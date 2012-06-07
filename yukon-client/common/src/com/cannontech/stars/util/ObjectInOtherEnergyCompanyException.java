package com.cannontech.stars.util;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class ObjectInOtherEnergyCompanyException extends RuntimeException {
	
	private LiteBase object;
	private YukonEnergyCompany yukonEnergyCompany;
	
	public ObjectInOtherEnergyCompanyException() {
		super();
	}
	
	public ObjectInOtherEnergyCompanyException(LiteBase obj, YukonEnergyCompany yukonEnergyCompany) {
		super();
		object = obj;
		this.yukonEnergyCompany = yukonEnergyCompany;
	}

	public YukonEnergyCompany getYukonEnergyCompany() {
		return yukonEnergyCompany;
	}

	public LiteBase getObject() {
		return object;
	}

}