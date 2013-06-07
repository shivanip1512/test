package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.program.model.ProgramState;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.messaging.message.loadcontrol.data.Program;

public class StateFilter implements UiFilter<DisplayablePao> {
    public static enum FilteredState {
        ALL,
        ACTIVE,
        SCHEDULED,
        INACTIVE
    }

    private ProgramService programService;

    // we either show active (or similar) or show inactive
    // if the filter is off, we don't create an instance of this class at all
    private FilteredState filteredState;

    public StateFilter(ProgramService programService, FilteredState filterState) {
        this.programService = programService;
        this.filteredState = filterState;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                if (filteredState == FilteredState.ALL) {
                    return true;
                }
                Program program = programService.getProgramForPao(pao);
                if (program == null) {
                    // Program is not in load management; it cannot be active,
                    // or inactive.
                    return false;
                }
                if (filteredState == FilteredState.ACTIVE) {
                    return program.isActive();
                }
                if (filteredState == FilteredState.SCHEDULED) {
                    return program.getProgramState() == ProgramState.SCHEDULED;
                }
                if (filteredState == FilteredState.INACTIVE) {
                    return !program.isActive()
                        && program.getProgramState() != ProgramState.SCHEDULED;
                }
                return true;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
