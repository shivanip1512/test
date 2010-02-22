package com.cannontech.common.search;

public interface Searcher<T> {
    public SearchResult<T> search(String queryString, YukonObjectCriteria criteria, int start, int count);
    public SearchResult<T> all(YukonObjectCriteria criteria, int start, int count);
}
