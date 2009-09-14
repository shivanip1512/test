package com.cannontech.common.bulk.filter;


public interface UiFilter<T> {
    public Iterable<SqlFilter> getSqlFilters();
    public Iterable<PostProcessingFilter<T>> getPostProcessingFilters();
}
