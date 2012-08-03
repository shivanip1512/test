package com.cannontech.common.search;


public interface UserGroupSearcher extends Searcher<UltraLightUserGroup> {
    public SearchResult<UltraLightUserGroup> search(String queryString, YukonObjectCriteria criteria);
}
