package com.cannontech.common.search.searcher;

import com.cannontech.common.search.criteria.YukonObjectCriteria;
import com.cannontech.common.search.result.SearchResults;

public interface Searcher<T> {
    public SearchResults<T> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResults<T> all(YukonObjectCriteria criteria, int start, int count);
}
