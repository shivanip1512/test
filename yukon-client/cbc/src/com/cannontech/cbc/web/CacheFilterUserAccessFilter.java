package com.cannontech.cbc.web;

import java.util.Set;

import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CBCArea;

public class CacheFilterUserAccessFilter implements CacheFilter
{
	PaoPermissionService paoPermissionService = (PaoPermissionService)YukonSpringHook.getBean("paoPermissionService");
	LiteYukonUser user;

	public boolean valid( Object o )
	{
		CBCArea a = (CBCArea)o;
		Set<Integer> set = paoPermissionService.getPaoIdsForUserPermission(user, Permission.PAO_VISIBLE);
		for( Integer i : set )
		{
			if( i.intValue() == a.getPaoID() )
				return true;
		}
		return false;
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
