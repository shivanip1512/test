package com.cannontech.web.search.searcher;

import com.cannontech.common.model.Direction;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.search.lucene.criteria.YukonObjectCriteria;

public interface Searcher<T> {
    public SearchResults<T> search(String queryString, YukonObjectCriteria criteria, int start, int count, String sortBy, Direction direction);
    public SearchResults<T> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResults<T> all(YukonObjectCriteria criteria, int start, int count);
}
