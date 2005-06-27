package com.cannontech.database.cache.functions;

import java.util.ArrayList;
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
    // the following is a duplicate of StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID
    public static final int DEFAULT_ENERGY_COMPANY_ID = -1;

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
 * Returns all the LiteEnergyCompany's that have customerID_ in it.
 * 
 * @param customerID_ int
 * @return LiteEnergyCompany
 */
public static LiteEnergyCompany[] getEnergyCompaniesByCustomer( int customerID_ )
{
	ArrayList enrgComps = new ArrayList( 16 );
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		for( int i = 0; i < cache.getAllEnergyCompanies().size(); i++ )
		{
			LiteEnergyCompany e = (LiteEnergyCompany)cache.getAllEnergyCompanies().get(i);

			for( int j = 0; j < e.getCiCustumerIDs().size(); i++ )
			{
				if( e.getCiCustumerIDs().elementAt(j) == customerID_ )
				{
					enrgComps.add( e );
					break; //move onto the next energycompany
				}
			}
		}	
	}

	LiteEnergyCompany[] cArr = new LiteEnergyCompany[ enrgComps.size() ];
	return (LiteEnergyCompany[])enrgComps.toArray( cArr );
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

/**
 * Returns value of the energy company property
 * @param user, rolePropertyID
 * @return String
 */
public static String getEnergyCompanyProperty(LiteYukonUser user, int rolePropertyID) {
	LiteEnergyCompany ec = getEnergyCompany( user );
	LiteYukonUser ecUser = getEnergyCompanyUser( ec );
	return AuthFuncs.getRolePropertyValue( ecUser, rolePropertyID );
}

}
