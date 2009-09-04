package com.cannontech.dr.controlarea.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.Range;
import com.cannontech.loadcontrol.data.LMControlArea;

public class PriorityFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMControlArea> mapper;

    private Range<Integer> filter;

    public PriorityFilter(ObjectMapper<DisplayablePao, LMControlArea> mapper,
            Range<Integer> filter) {
        this.mapper = mapper;
        this.filter = filter;
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
                    && filter.intersects(controlArea.getCurrentPriority() <= 0
                                         ? 1 : controlArea.getCurrentPriority());
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
