package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
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
        ArrayList<LiteYukonRoleProperty> list = new ArrayList<LiteYukonRoleProperty>(64);
        synchronized (databaseCache) {
            List<LiteYukonRoleProperty> props = databaseCache.getAllYukonRoleProperties();

            for (int i = 0; i < props.size(); i++) {
                LiteYukonRoleProperty p = props.get(i);
                if (p.getRoleID() == roleID_)
                    list.add(p);
            }
        }

        LiteYukonRoleProperty[] retVal = new LiteYukonRoleProperty[list.size()];
        return list.toArray(retVal);
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


    public LiteYukonRole getLiteRole(Integer rolePropID) {
        List<LiteYukonRoleProperty> roleProps = Collections.emptyList();
        List<LiteYukonRole> roles = Collections.emptyList();
        synchronized (databaseCache) {
            roleProps = databaseCache.getAllYukonRoleProperties();
            roles = databaseCache.getAllYukonRoles();
            for (LiteYukonRoleProperty property : roleProps) {
                if (property.getRolePropertyID() == rolePropID) {
                    for (LiteYukonRole role : roles) {
                        if (property.getRoleID() == role.getRoleID()) {
                            return role;
                        }
                    }
                }
            }
        }
        throw new NotFoundException("Role ID Could not be found");
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
