package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class StateFilter implements UiFilter<ControllablePao> {
    private ProgramService programService;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private boolean showActive;

    public StateFilter(ProgramService programService, boolean showActive) {
        this.programService = programService;
        this.showActive = showActive;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> retVal =
            new ArrayList<PostProcessingFilter<ControllablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<ControllablePao>() {

            @Override
            public boolean matches(ControllablePao pao) {
                LMProgramBase program = programService.getProgramForPao(pao);
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
