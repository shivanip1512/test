package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.MutableRange;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class PriorityFilter implements UiFilter<DisplayablePao> {
    private ProgramService programService;

    private MutableRange<Integer> filter;

    public PriorityFilter(ProgramService programService, MutableRange<Integer> filter) {
        this.programService = programService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
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
