package com.cannontech.dr.controlarea.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;

public class StateFilter implements UiFilter<ControllablePao> {
    private ControlAreaService controlAreaService;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private boolean showActive;

    public StateFilter(ControlAreaService controlAreaService, boolean showActive) {
        this.controlAreaService = controlAreaService;
        this.showActive = showActive;
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
