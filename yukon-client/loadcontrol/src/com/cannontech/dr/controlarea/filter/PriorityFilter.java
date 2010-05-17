package com.cannontech.dr.controlarea.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.Range;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;

public class PriorityFilter implements UiFilter<ControllablePao> {
    private ControlAreaService controlAreaService;

    private Range<Integer> filter;

    public PriorityFilter(ControlAreaService controlAreaService, Range<Integer> filter) {
        this.controlAreaService = controlAreaService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> retVal =
            new ArrayList<PostProcessingFilter<ControllablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<ControllablePao>() {

            @Override
            public boolean matches(ControllablePao pao) {
                LMControlArea controlArea = controlAreaService.getControlAreaForPao(pao);
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
