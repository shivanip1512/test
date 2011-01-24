/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.data.lite.LiteBase;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ObjectInOtherEnergyCompanyException extends Exception {
	
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

	/**
	 * @return
	 */
	public YukonEnergyCompany getYukonEnergyCompany() {
		return yukonEnergyCompany;
	}

	/**
	 * @return
	 */
	public LiteBase getObject() {
		return object;
	}

}
