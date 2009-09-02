package com.cannontech.common.bulk.filter;

import java.util.List;

public interface UiFilter<T> {
    public List<SqlFilter> getSqlFilters();
    public List<PostProcessingFilter<T>> getPostProcessingFilters();
}
