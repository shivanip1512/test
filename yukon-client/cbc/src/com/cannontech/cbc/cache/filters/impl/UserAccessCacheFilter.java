package com.cannontech.cbc.cache.filters.impl;


import com.cannontech.cbc.cache.filters.CacheFilter;
import com.cannontech.core.authorization.service.impl.PaoAuthorizationServiceImpl;

import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.*;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.*;

public class UserAccessCacheFilter implements CacheFilter<StreamableCapObject>
{
    PaoAuthorizationServiceImpl paoPermissionService = YukonSpringHook.getBean("paoAuthorizationService",PaoAuthorizationServiceImpl.class);   
    LiteYukonUser user;
    
    public UserAccessCacheFilter(LiteYukonUser user) {
        setUser(user);
    }
    
    public void setUser( LiteYukonUser user ) {
        this.user = user;
    }
    
    /**
     *  This is a little backwards from what you would think. The permission table is being used as a deny table
     *  for CBC Pao's. If it is on the list, we should not be able to see it at all.
     *  
     *  There is an exception. If the user is denied, they will not be able to see it.
     *  
     *  However if they belong to multiple groups, only one groups needs to not be denied in order to see it.
     */
	public boolean valid(StreamableCapObject capObject) {
	    if (!(capObject instanceof CBCArea || 
	          capObject instanceof CBCSpecialArea)) return false;
	    
        int paoId = capObject.getCcId();
        String paoName = capObject.getCcName();
        
        LiteYukonPAObject obj = new LiteYukonPAObject(paoId, paoName);
        boolean ret = paoPermissionService.isAuthorized(user, Permission.PAO_VISIBLE, obj );
        return ret;
	}
	
}
