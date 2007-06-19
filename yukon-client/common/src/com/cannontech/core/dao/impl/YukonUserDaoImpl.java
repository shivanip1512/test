package com.cannontech.core.dao.impl;

import java.util.TimeZone;

import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 * A undefined generated comment
 */
public final class YukonUserDaoImpl implements YukonUserDao
{
    private IDatabaseCache databaseCache;
    private EnergyCompanyDao energyCompanyDao = null;
    
	/**
	 * Constructor for DaoFactory.getYukonUserDao().
	 */
	public YukonUserDaoImpl()
	{
		super();
	}

   
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonUserDao#getLiteYukonUser(int)
     */
	public LiteYukonUser getLiteYukonUser( int userID_ )
	{
		synchronized( databaseCache )
		{
			return (LiteYukonUser)
				databaseCache.getAllUsersMap().get( new Integer(userID_) );
		}
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonUserDao#getLiteYukonUser(java.lang.String)
     */
	public LiteYukonUser getLiteYukonUser( String userName_ )
	{
		if( userName_ == null )
			return null;

		synchronized( databaseCache )
		{
			java.util.List users = databaseCache.getAllYukonUsers();
         
			for( int j = 0; j < users.size(); j++ )
			{
				if( userName_.equals( ((LiteYukonUser)users.get(j)).getUsername() ) )
					return (LiteYukonUser)users.get(j);
			}
   
			return null;
		}	
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonUserDao#getLiteContact(int)
     */
	public LiteContact getLiteContact ( int userID_ )
	{
	    synchronized(databaseCache) 
	    {
	        return databaseCache.getAContactByUserID(userID_);
	    }
	}
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }

    public TimeZone getUserTimeZone(LiteYukonUser user) {
        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        TimeZone timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);
        return timeZone;
    }

}
