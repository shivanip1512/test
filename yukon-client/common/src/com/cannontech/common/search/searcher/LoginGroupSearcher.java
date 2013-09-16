package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightLoginGroup;


public interface LoginGroupSearcher extends Searcher<UltraLightLoginGroup> {
    public SearchResults<UltraLightLoginGroup> search(String queryString, YukonObjectCriteria criteria);
}
