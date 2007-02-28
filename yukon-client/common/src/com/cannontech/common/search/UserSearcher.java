package com.cannontech.common.search;


public interface UserSearcher {
    public SearchResult<UltraLightYukonUser> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightYukonUser> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightYukonUser> allUsers(YukonObjectCriteria criteria, int start, int count);
}
