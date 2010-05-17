package com.cannontech.dr.filter;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.model.ControllablePao;
import com.google.common.collect.Lists;

public class NotPaoTypeFilter implements UiFilter<ControllablePao> {
    private PaoType paoTypeNotToBe;

    public NotPaoTypeFilter(PaoType paoTypeNotToBe) {
        this.paoTypeNotToBe = paoTypeNotToBe;
    }

    @Override
    public Iterable<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> filters = Lists.newArrayList();
        filters.add(new PostProcessingFilterAdapter<ControllablePao>() {
            @Override
            public boolean matches(ControllablePao pao) {
                return !pao.getPaoIdentifier().getPaoType().equals(paoTypeNotToBe);
            }});
        return filters;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        return null;
    }
}