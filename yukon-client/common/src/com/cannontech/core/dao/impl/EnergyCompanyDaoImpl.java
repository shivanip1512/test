package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Energy company related convenience funcs
 * @author: alauinger 
 */
public final class EnergyCompanyDaoImpl implements EnergyCompanyDao {
    // the following is a duplicate of StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID
    public final int DEFAULT_ENERGY_COMPANY_ID = -1;

    private AuthDao authDao;
    private YukonUserDao yukonUserDao;
    private IDatabaseCache databaseCache;
    
private EnergyCompanyDaoImpl() {
	super();
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompany(int)
 */
public LiteEnergyCompany getEnergyCompany(int energyCompanyID) {
		synchronized( databaseCache ) {
			for(Iterator<LiteEnergyCompany> i = databaseCache.getAllEnergyCompanies().iterator(); i.hasNext();) {
				LiteEnergyCompany e = i.next();
				if(e.getEnergyCompanyID() == energyCompanyID) {
					return e;
				}
			}	
		}
	return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompany(com.cannontech.database.data.lite.LiteYukonUser)
 */
public LiteEnergyCompany getEnergyCompany(LiteYukonUser user) {
	synchronized( databaseCache ) {
		Map<LiteYukonUser, LiteEnergyCompany> m = databaseCache.getAllUserEnergyCompanies();
		LiteEnergyCompany liteEnergyCompany = m.get(user);
        if (liteEnergyCompany == null) {
            return getEnergyCompany(DEFAULT_ENERGY_COMPANY_ID);
        }
        return liteEnergyCompany;
	}	
}

    @Override
    public LiteEnergyCompany getEnergyCompanyByName(final String energyCompanyName) {
        synchronized (databaseCache) {
            List<LiteEnergyCompany> energyCompanies = databaseCache.getAllEnergyCompanies();
            for (final LiteEnergyCompany energyCompany : energyCompanies) {
                String name = energyCompany.getName();
                if (name.equalsIgnoreCase(energyCompanyName)) return energyCompany;
            }
        }
        throw new NotFoundException("Energy Company with name: " + energyCompanyName + " not found.");
    }

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompaniesByCustomer(int)
 */
public LiteEnergyCompany[] getEnergyCompaniesByCustomer( int customerID_ )
{
	List<LiteEnergyCompany> enrgComps = new ArrayList<LiteEnergyCompany>( 16 );
	synchronized( databaseCache )
	{
		for( int i = 0; i < databaseCache.getAllEnergyCompanies().size(); i++ )
		{
			LiteEnergyCompany e = databaseCache.getAllEnergyCompanies().get(i);

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
	return enrgComps.toArray( cArr );
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyUser(com.cannontech.database.data.lite.LiteEnergyCompany)
 */
public LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company) {
	return yukonUserDao.getLiteYukonUser(company.getUserID());
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyUser(int)
 */
public LiteYukonUser getEnergyCompanyUser(int energyCompanyID) {
	LiteEnergyCompany ec = getEnergyCompany(energyCompanyID);
	return (ec == null ? null : getEnergyCompanyUser(ec));
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyProperty(com.cannontech.database.data.lite.LiteYukonUser, int)
 */
public String getEnergyCompanyProperty(LiteYukonUser user, int rolePropertyID) {
	LiteEnergyCompany ec = getEnergyCompany( user );
	LiteYukonUser ecUser = getEnergyCompanyUser( ec );
	return authDao.getRolePropertyValue( ecUser, rolePropertyID );
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyProperty(com.cannontech.database.data.lite.LiteEnergyCompany, int)
 */
public String getEnergyCompanyProperty(LiteEnergyCompany ec, int rolePropertyID) {
    return authDao.getRolePropertyValue(getEnergyCompanyUser(ec), rolePropertyID);
}

public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
}

public void setAuthDao(AuthDao authDao) {
    this.authDao = authDao;
}

public void setYukonUserDao(YukonUserDao yukonUserDao) {
    this.yukonUserDao = yukonUserDao;
}
}
