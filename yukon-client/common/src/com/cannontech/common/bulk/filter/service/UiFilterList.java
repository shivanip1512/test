package com.cannontech.common.bulk.filter.service;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class UiFilterList <T> implements UiFilter<T> {
    private List<UiFilter<T>> filters;

    public static <U> UiFilter<U> wrap(List<UiFilter<U>> filters) {
        return new UiFilterList<U>(filters);
    }

    private UiFilterList(List<UiFilter<T>> filters) {
        this.filters = filters;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        List<Iterable<SqlFilter>> retVal = Lists.newArrayList();
        for (UiFilter<T> filter : filters) {
            Iterable<SqlFilter> ppFilter = filter.getSqlFilters();
            if (ppFilter != null) {
                retVal.add(ppFilter);
            }
        }
        return Iterables.concat(retVal);
    }

    @Override
    public Iterable<PostProcessingFilter<T>> getPostProcessingFilters() {
        List<Iterable<PostProcessingFilter<T>>> retVal = Lists.newArrayList();
        for (UiFilter<T> filter : filters) {
            Iterable<PostProcessingFilter<T>> ppFilter = filter.getPostProcessingFilters();
            if (ppFilter != null) {
                retVal.add(ppFilter);
            }
        }
        return Iterables.concat(retVal);
    }
}
