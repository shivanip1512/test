package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightUserGroup;


public interface UserGroupSearcher extends Searcher<UltraLightUserGroup> {
    public SearchResults<UltraLightUserGroup> search(String queryString, YukonObjectCriteria criteria);
}
