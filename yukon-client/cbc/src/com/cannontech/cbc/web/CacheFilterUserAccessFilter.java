package com.cannontech.cbc.web;


import com.cannontech.core.authorization.service.impl.PaoAuthorizationServiceImpl;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.*;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CBCArea;

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
		CBCArea a = (CBCArea)o;
		LiteYukonPAObject obj = new LiteYukonPAObject(a.getPaoID(), a.getPaoName());
        boolean ret = paoPermissionService.isAuthorized(user, Permission.PAO_DENY_VISIBLE, obj );
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
