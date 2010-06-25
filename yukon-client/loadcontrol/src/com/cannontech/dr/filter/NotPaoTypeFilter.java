package com.cannontech.dr.filter;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public class NotPaoTypeFilter implements UiFilter<DisplayablePao> {
    private PaoType paoTypeNotToBe;

    public NotPaoTypeFilter(PaoType paoTypeNotToBe) {
        this.paoTypeNotToBe = paoTypeNotToBe;
    }

    @Override
    public Iterable<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(new PostProcessingFilterAdapter<DisplayablePao>() {
            @Override
            public boolean matches(DisplayablePao pao) {
                return !pao.getPaoIdentifier().getPaoType().equals(paoTypeNotToBe);
            }});
        return filters;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        return null;
    }
}