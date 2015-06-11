package com.cannontech.cbc.cache.filters.impl;


import com.cannontech.cbc.cache.filters.CacheFilter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.spring.YukonSpringHook;

public class UserAccessCacheFilter implements CacheFilter<StreamableCapObject> {
    
    private PaoAuthorizationService paoAuth = 
            YukonSpringHook.getBean("paoAuthorizationService", PaoAuthorizationService.class);   
    private LiteYukonUser user;
    
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
        
        if (!(capObject instanceof Area || 
              capObject instanceof SpecialArea)) return false;
        
        int paoId = capObject.getCcId();
        PaoType type = PaoType.getForDbString(capObject.getCcType());
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, type);
        
        return paoAuth.isAuthorized(user, Permission.PAO_VISIBLE, paoIdentifier);
    }
    
}