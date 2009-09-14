package com.cannontech.common.bulk.filter.service;

import java.util.Comparator;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;

public interface FilterService {
    public <T> SearchResult<T> filter(UiFilter<? super T> filter,
            Comparator<? super T> sorter, int startIndex, int count,
            RowMapperWithBaseQuery<T> rowMapper);
}
