package com.cannontech.common.bulk.filter.service;

import java.util.Comparator;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;

public interface FilterDao {
    public <T> SearchResult<T> filter(UiFilter<T> filter, Comparator<? super T> sorter, int startIndex, int count,
        RowMapperWithBaseQuery<T> rowMapper);
}
