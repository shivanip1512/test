package com.cannontech.common.search;


public interface UserSearcher extends Searcher<UltraLightYukonUser> {
    public SearchResult<UltraLightYukonUser> search(String queryString, YukonObjectCriteria criteria);
}
