package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.Range;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class PriorityFilter implements UiFilter<ControllablePao> {
    private ProgramService programService;

    private Range<Integer> filter;

    public PriorityFilter(ProgramService programService, Range<Integer> filter) {
        this.programService = programService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> retVal =
            new ArrayList<PostProcessingFilter<ControllablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<ControllablePao>() {

            @Override
            public boolean matches(ControllablePao pao) {
                LMProgramBase program = programService.getProgramForPao(pao);
                // TODO:  this logic is duplicated in ProgramDisplayField
                return program != null && filter.intersects(program.getStartPriority() <= 0
                                                            ? 1 : program.getStartPriority());
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
