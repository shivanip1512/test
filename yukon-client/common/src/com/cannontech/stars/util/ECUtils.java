/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

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
	public static List<LiteStarsEnergyCompany> getAllDescendants(final LiteStarsEnergyCompany parent) {
		List<LiteStarsEnergyCompany> descendants = new ArrayList<LiteStarsEnergyCompany>();
		descendants.add( parent );
		
        for (final LiteStarsEnergyCompany child : parent.getChildren()) {
            descendants.addAll( getAllDescendants(child) );
        }
        
		return descendants;
	}
	
    /**
     * Get all the energy companies that the energy company 
     * belongs to (directly or indirectly), including itself
     *
     * This method returns a list of energy companies starting with the child energy company and ending
     * with the energy company right below the default energy company. 
     *
     * @param childEC
     * @return
     */	
    public static List<LiteStarsEnergyCompany> getAllAscendants(LiteStarsEnergyCompany childEC) {
        List<LiteStarsEnergyCompany> ascendants = new ArrayList<LiteStarsEnergyCompany>();
        ascendants.add( childEC );
        
        while(childEC.getParent() != null) {
            ascendants.add(childEC.getParent());
            childEC = childEC.getParent();
        }
        
        return ascendants;
    }

    /**
	 * @return Whether company1 is a descendant of company2
	 */
	public static boolean isDescendantOf(LiteStarsEnergyCompany company1, LiteStarsEnergyCompany company2) {
		if (company1.equals( company2 )) return false;
        List<LiteStarsEnergyCompany> descendants = getAllDescendants( company2 );
		return descendants.contains( company1 );
	}
	
	public static boolean isDefaultEnergyCompany(LiteStarsEnergyCompany company) {
		return (company == null) ? false : company.getLiteID() == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
	}
	
	public static boolean isSingleEnergyCompany(LiteStarsEnergyCompany company) {
		String value = company.getEnergyCompanySetting( EnergyCompanyRole.SINGLE_ENERGY_COMPANY );
		return (value == null) || Boolean.valueOf(value).booleanValue();
	}

    /**
     * 
     * @param energyCompanyList
     * @return
     */
    public static List<Integer> toIdList(List<LiteStarsEnergyCompany> energyCompanyList) {
        List<Integer> idList = new ArrayList<Integer>(energyCompanyList.size());
        for (final LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            idList.add(energyCompany.getEnergyCompanyId());
        }
        return idList;
    }
}
