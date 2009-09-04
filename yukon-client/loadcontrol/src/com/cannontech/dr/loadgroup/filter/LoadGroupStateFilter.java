package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;

public class LoadGroupStateFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private boolean showActive;

    public LoadGroupStateFilter(ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper,
            boolean showActive) {
        this.mapper = mapper;
        this.showActive = showActive;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                LMDirectGroupBase group = mapper.map(pao);
                return group != null
                    && (group.isActive() && showActive
                    || !group.isActive() && !showActive);
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
