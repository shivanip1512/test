/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ObjectInOtherEnergyCompanyException extends Exception {
	
	private LiteBase object = null;
	private LiteStarsEnergyCompany energyCompany = null;
	
	public ObjectInOtherEnergyCompanyException() {
		super();
	}
	
	public ObjectInOtherEnergyCompanyException(LiteBase obj, LiteStarsEnergyCompany company) {
		super();
		object = obj;
		energyCompany = company;
	}

	/**
	 * @return
	 */
	public LiteStarsEnergyCompany getEnergyCompany() {
		return energyCompany;
	}

	/**
	 * @return
	 */
	public LiteBase getObject() {
		return object;
	}

}
