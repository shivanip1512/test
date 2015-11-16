package com.cannontech.web.search.searcher;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightLoginGroup;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public interface LoginGroupSearcher extends Searcher<UltraLightLoginGroup> {
    
    SearchResults<UltraLightLoginGroup> search(String queryString, YukonObjectCriteria criteria);
    
}