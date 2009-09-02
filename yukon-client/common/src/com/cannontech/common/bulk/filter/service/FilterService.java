package com.cannontech.common.bulk.filter.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;

public interface FilterService {
    public <T> SearchResult<T> filter(List<UiFilter<T>> filters,
            Comparator<T> primarySorter, Comparator<T> secondarySorter,
            int startIndex, int count, RowMapperWithBaseQuery<T> rowMapper);
}
