package com.cannontech.cbc.web;


import com.cannontech.core.authorization.service.impl.PaoAuthorizationServiceImpl;

import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.*;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.*;

public class CacheFilterUserAccessFilter implements CacheFilter
{
    PaoAuthorizationServiceImpl paoPermissionService = YukonSpringHook.getBean("paoAuthorizationService",PaoAuthorizationServiceImpl.class);   
    LiteYukonUser user;    

    /**
     *  This is a little backwards from what you would think. The permission table is being used as a deny table
     *  for CBC Pao's. If it is on the list, we should not be able to see it at all.
     *  
     *  There is an exception. If the user is denied, they will not be able to see it.
     *  
     *  However if they belong to multiple groups, only one groups needs to not be denied in order to see it.
     */
	public boolean valid( Object o )
	{
        int id;
        String name;
        
        //It is this way until StreamableCapObject is fixed. In the cache right now, the values in the parent are null.
        if( o instanceof CBCArea)
        {
            id = ((CBCArea)o).getPaoID();
            name = ((CBCArea)o).getPaoName();
        }
        else if( o instanceof CBCSpecialArea )
        {
            id = ((CBCSpecialArea)o).getPaoID();
            name = ((CBCSpecialArea)o).getPaoName();           
        }
        else
        {
            return false;
        }
            
        LiteYukonPAObject obj = new LiteYukonPAObject(id, name);
        boolean ret = paoPermissionService.isAuthorized(user, Permission.PAO_VISIBLE, obj );
        return ret;
	}
	
	public CacheFilterUserAccessFilter(LiteYukonUser user)
	{
        setUser(user);
	}
	
	public void setUser( LiteYukonUser user )
	{
		this.user = user;
	}
}
