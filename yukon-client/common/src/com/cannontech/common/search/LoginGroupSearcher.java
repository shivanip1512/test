package com.cannontech.common.search;


public interface LoginGroupSearcher {
    public SearchResult<UltraLightLoginGroup> search(String queryString, YukonObjectCriteria criteria);
    public SearchResult<UltraLightLoginGroup> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<UltraLightLoginGroup> allLoginGroups(YukonObjectCriteria criteria, int start, int count);
}
