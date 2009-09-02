package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class StateFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMProgramBase> mapper;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private boolean showActive;

    public StateFilter(ObjectMapper<DisplayablePao, LMProgramBase> mapper,
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
                LMProgramBase program = mapper.map(pao);
                return program != null
                    && (program.isActive() && showActive
                    || !program.isActive() && !showActive);
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
