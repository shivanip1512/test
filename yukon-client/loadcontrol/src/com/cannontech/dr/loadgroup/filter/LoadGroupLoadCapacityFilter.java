package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.Range;
import com.cannontech.dr.loadgroup.service.LoadGroupService;

public class LoadGroupLoadCapacityFilter implements UiFilter<DisplayablePao> {
    private LoadGroupService loadGroupService;
    private Range<Double> filter;
    
    public LoadGroupLoadCapacityFilter(LoadGroupService loadGroupService, Range<Double> filter) {
        this.loadGroupService = loadGroupService;
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
