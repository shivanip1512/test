package com.cannontech.cbc.cache;

import com.cannontech.cbc.cache.filters.impl.UserAccessCacheFilter;
import com.cannontech.cbc.cache.impl.FilterCapControlCacheImpl;
import com.cannontech.database.data.lite.LiteYukonUser;

public class FilterCacheFactory {
    
    private CapControlCache cache;
    
    public void setCache(CapControlCache cache) {
        this.cache = cache;
    }
    
    public CapControlCache createUserAccessFilteredCache(final LiteYukonUser user) {
        
        FilterCapControlCacheImpl filteredCache = new FilterCapControlCacheImpl();
        filteredCache.setFilter(new UserAccessCacheFilter(user));
        filteredCache.setCache(cache);
        
        return filteredCache;
    }
    
}