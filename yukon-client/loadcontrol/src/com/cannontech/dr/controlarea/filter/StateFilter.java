package com.cannontech.dr.controlarea.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.loadcontrol.data.LMControlArea;

public class StateFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMControlArea> mapper;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private boolean showActive;

    public StateFilter(ObjectMapper<DisplayablePao, LMControlArea> mapper,
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
                LMControlArea controlArea = mapper.map(pao);
                return controlArea != null
                    && (controlArea.getControlAreaState() != LMControlArea.STATE_INACTIVE && showActive
                    || controlArea.getControlAreaState() == LMControlArea.STATE_INACTIVE && !showActive);
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
