package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.Range;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class StartStopFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMProgramBase> mapper;

    private Range<Date> filter;
    private boolean isStart;

    public StartStopFilter(ObjectMapper<DisplayablePao, LMProgramBase> mapper,
            Range<Date> filter, boolean isStart) {
        this.mapper = mapper;
        this.filter = filter;
        this.isStart = isStart;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                LMProgramBase program = mapper.map(pao);
                boolean retVal = program != null && filter.intersects((isStart
                        ? program.getStartTime() : program.getStopTime()).getTime());
                return retVal;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
