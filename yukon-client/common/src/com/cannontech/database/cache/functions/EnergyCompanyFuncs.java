package com.cannontech.database.cache.functions;

import java.util.Map;

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
 * Return the energy company this user belongs to.
 * @param user
 * @return LiteEnergyCompany
 */
public static LiteEnergyCompany getEnergyCompany(LiteYukonUser user) {
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache ) {
		Map m = cache.getAllUserEnergyCompanies();
		return (LiteEnergyCompany) m.get(user);
	}	
}
}
