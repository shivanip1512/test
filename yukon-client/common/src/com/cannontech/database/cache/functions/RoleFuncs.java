package com.cannontech.database.cache.functions;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * A collection of methods to handle Roles and property lookups for Groups and Users
 * @author rneuharth
 */
public class RoleFuncs 
{
	/**
	 * Returns the LiteYukonRoleProperty[] that are owned by the given roleID_.
	 * Returns a zero length array if the role_ is null. 
	 * @param LiteYukonRole
	 * @return LiteYukonRoleProperty[]
	 */
	public static LiteYukonRoleProperty[] getRoleProperties( int roleID_ ) 
	{
		Vector vect = new Vector(64);

		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
			List props  = cache.getAllYukonRoleProperties();
						
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


	/**
	 * Returns the value for a given user and role property.
	 * If no value is found then defaultValue is returned for convenience.
	 * @param user
	 * @param roleProperty
	 * @return String
	 */
	public static String getRolePropertyValue(int userID, int rolePropertyID, String defaultValue) 
	{		
		LiteYukonUser user = null;
			
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
			//find the user with the given userID (may be slow in the future)
			List allUsers = cache.getAllYukonUsers();
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
		
		return AuthFuncs.getRolePropertyValue( user, rolePropertyID, defaultValue );
	}
	

	/**
	 * Dont let anyone instantiate me
	 * @see java.lang.Object#Object()
	 */
	private RoleFuncs() {
	}
		
}
