package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.Range;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;

public class LoadGroupLoadCapacityFilter implements UiFilter<DisplayablePao> {
    private ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper;
    private Range<Double> filter;
    
    public LoadGroupLoadCapacityFilter(ObjectMapper<DisplayablePao, LMDirectGroupBase> mapper, Range<Double> filter) {
        this.mapper = mapper;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao object) {
                return true;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
