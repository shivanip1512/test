package com.cannontech.common.search;


public interface LoginGroupSearcher extends Searcher<UltraLightLoginGroup> {
    public SearchResult<UltraLightLoginGroup> search(String queryString, YukonObjectCriteria criteria);
}
