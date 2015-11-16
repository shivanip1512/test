package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightUserGroup;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;


public interface UserGroupSearcher extends Searcher<UltraLightUserGroup> {
    
    SearchResults<UltraLightUserGroup> search(String queryString, YukonObjectCriteria criteria);
    
}