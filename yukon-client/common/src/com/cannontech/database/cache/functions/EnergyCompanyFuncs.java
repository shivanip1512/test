package com.cannontech.database.cache.functions;

import java.util.Iterator;
import java.util.Map;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Energy company related convenience funcs
 * @author: alauinger 
 */
public final class EnergyCompanyFuncs {

private EnergyCompanyFuncs() {
	super();
}

/**
 * Returns the lite energy company with the given energy company id.
 * @param energyCompanyID
 * @return LiteEnergyCompany
 */
public static LiteEnergyCompany getEnergyCompany(int energyCompanyID) {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache ) {
			for(Iterator i = cache.getAllEnergyCompanies().iterator(); i.hasNext();) {
				LiteEnergyCompany e = (LiteEnergyCompany) i.next();
				if(e.getEnergyCompanyID() == energyCompanyID) {
					return e;
				}
			}	
		}
	return null;
}

/**
 * Return the energy company this user belongs to.
 * @param user
 * @return LiteEnergyCompany
 */
public static LiteEnergyCompany getEnergyCompany(LiteYukonUser user) {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache ) {
		Map m = cache.getAllUserEnergyCompanies();
		return (LiteEnergyCompany) m.get(user);
	}	
}

/**
 * Returns the energy companies lite user 
 * @param company
 * @return LiteYukonUser
 */
public static LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company) {
	return YukonUserFuncs.getLiteYukonUser(company.getUserID());
}

/**
 * Returns the energy companies lite user
 * @param energyCompanyID
 * @return LiteYukonUser
 */
public static LiteYukonUser getEnergyCompanyUser(int energyCompanyID) {
	LiteEnergyCompany ec = getEnergyCompany(energyCompanyID);
	return (ec == null ? null : getEnergyCompanyUser(ec));
}

}
