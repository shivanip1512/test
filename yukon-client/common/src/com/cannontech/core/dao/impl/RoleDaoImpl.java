package com.cannontech.core.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.yukon.IDatabaseCache;

/**
 * A collection of methods to handle Roles and property lookups for Groups and Users
 * @author rneuharth
 */
public class RoleDaoImpl implements RoleDao 
{
    private AuthDao authDao;
    private IDatabaseCache databaseCache;
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#getRoleProperties(int)
     */
	public LiteYukonRoleProperty[] getRoleProperties( int roleID_ ) 
	{
		Vector vect = new Vector(64);
		synchronized(databaseCache) 
		{
			List props  = databaseCache.getAllYukonRoleProperties();
						
			for(int i = 0; i < props.size(); i++ ) 
			{
				LiteYukonRoleProperty p = (LiteYukonRoleProperty)props.get(i);
				if( p.getRoleID() == roleID_ )
					vect.add( p );
			}
		}
		
		LiteYukonRoleProperty[] retVal = new LiteYukonRoleProperty[vect.size()];
		return (LiteYukonRoleProperty[])vect.toArray( retVal );
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#getGlobalPropertyValue(int)
     */
	public String getGlobalPropertyValue( int rolePropertyID_ ) 
	{		
		LiteYukonRoleProperty p = 
			authDao.getRoleProperty( rolePropertyID_ );

		String val = null;
		if( p != null )
			val = authDao.getRolePropValueGroup(
				authDao.getGroup(YukonGroupRoleDefs.GRP_YUKON),
				p.getRolePropertyID(),
				p.getDefaultValue() );

		return val;
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#getRolePropertyValue(int, int, java.lang.String)
     */
	public String getRolePropertyValue(int userID, int rolePropertyID, String defaultValue) 
	{		
		LiteYukonUser user = null;
			
		synchronized(databaseCache) 
		{
			//find the user with the given userID (may be slow in the future)
			List allUsers = databaseCache.getAllYukonUsers();
			for( int i = 0; i < allUsers.size(); i++ )
			{
				LiteYukonUser u = (LiteYukonUser)allUsers.get(i); 
				if( u.getUserID() == userID )
				{
					user = u;
					break;
				}				
			}
			
		}
		
		return authDao.getRolePropertyValue( user, rolePropertyID );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#getRolePropValueGroup(int, int, java.lang.String)
     */
	public String getRolePropValueGroup(int groupID, int rolePropertyID, String defaultValue) 
	{		
		LiteYukonGroup group = null;
			
		synchronized(databaseCache) 
		{
			//find the group with the given groupID (may be slow in the future)
			List allgrps = databaseCache.getAllYukonGroups();
			for( int i = 0; i < allgrps.size(); i++ )
			{
				LiteYukonGroup u = (LiteYukonGroup)allgrps.get(i); 
				if( u.getGroupID() == groupID )
				{
					group = u;
					break;
				}				
			}
		
		}
		
		return authDao.getRolePropValueGroup( group, rolePropertyID, defaultValue );
	}
    
    public LiteYukonRole getLiteRole (Integer rolePropID) {
        List<LiteYukonRoleProperty> roleProps = Collections.EMPTY_LIST;
        List<LiteYukonRole> roles = Collections.EMPTY_LIST;
        synchronized (databaseCache) {
            roleProps   = databaseCache.getAllYukonRoleProperties();
            roles = databaseCache.getAllYukonRoles();
        }        
        for (LiteYukonRoleProperty property : roleProps) {
           if (property.getRolePropertyID() == rolePropID.intValue())
           {
            for (LiteYukonRole role : roles) {
                if (property.getRoleID() == role.getRoleID())
                {
                    return role;
                }
            }
           }
        }
        throw new NotFoundException ("Role ID Could not be found");
    }

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.RoleDao#hasLoadedGlobals()
     */
	public boolean hasLoadedGlobals()
	{
		return authDao.getGroup(YukonGroupRoleDefs.GRP_YUKON) != null;
	}

	/**
	 * Dont let anyone instantiate me
	 * @see java.lang.Object#Object()
	 */
	public RoleDaoImpl() {
	}


    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
	
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
