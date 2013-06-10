package com.cannontech.dr.controlarea.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.MutableRange;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;

public class PriorityFilter implements UiFilter<DisplayablePao> {
    private ControlAreaService controlAreaService;

    private MutableRange<Integer> filter;

    public PriorityFilter(ControlAreaService controlAreaService, MutableRange<Integer> filter) {
        this.controlAreaService = controlAreaService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                ControlAreaItem controlArea = controlAreaService.getControlAreaForPao(pao);
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
