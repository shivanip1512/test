/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.yukon.EnergyCompanyRole;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ECUtils {
	
	public static final int RIGHT_SHOW_ADDTL_PROTOCOLS = 0x20000000;
	
	/**
	 * Get all the energy companies that belongs to (directly or indirectly)
	 * this energy company, including itself
	 */
	public static ArrayList getAllDescendants(LiteStarsEnergyCompany parent) {
		ArrayList descendants = new ArrayList();
		descendants.add( parent );
		
		for (int i = 0; i < parent.getChildren().size(); i++) {
			LiteStarsEnergyCompany child = (LiteStarsEnergyCompany) parent.getChildren().get(i);
			descendants.addAll( getAllDescendants(child) );
		}
		
		return descendants;
	}
	
	/**
	 * @return Whether company1 is a descendant of company2
	 */
	public static boolean isDescendantOf(LiteStarsEnergyCompany company1, LiteStarsEnergyCompany company2) {
		if (company1.equals( company2 )) return false;
		ArrayList descendants = getAllDescendants( company2 );
		return descendants.contains( company1 );
	}
	
	public static boolean isDefaultEnergyCompany(LiteStarsEnergyCompany company) {
		return company.getLiteID() == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
	}
	
	public static boolean isSingleEnergyCompany(LiteStarsEnergyCompany company) {
		String value = company.getEnergyCompanySetting( EnergyCompanyRole.SINGLE_ENERGY_COMPANY );
		return (value == null) || Boolean.valueOf(value).booleanValue();
	}

	public static boolean hasRight(LiteStarsEnergyCompany energyCompany, int rightCode) {
		String value = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTIONAL_PRODUCT_DEV );
		if (value == null) return false;
		
		try {
			int bitMask = Integer.parseInt(value, 16);
			return (bitMask & rightCode) != 0;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

}
