package com.cannontech.common.bulk.filter.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.result.SearchResults;

public interface FilterDao {
    
    /** Filter and sort with paging. */
    <T> SearchResults<T> filter(UiFilter<T> filter, Comparator<? super T> sorter, int startIndex, int count,
        RowMapperWithBaseQuery<T> rowMapper);

    /** Filter and sort without paging. */
    <T> List<T> filter(UiFilter<T> filter, Comparator<? super T> sorter, RowMapperWithBaseQuery<T> rowMapper);
}
