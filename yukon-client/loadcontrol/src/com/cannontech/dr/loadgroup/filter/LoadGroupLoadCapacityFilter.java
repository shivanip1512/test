package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.Range;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.model.ControllablePao;

public class LoadGroupLoadCapacityFilter implements UiFilter<ControllablePao> {
    private LoadGroupService loadGroupService;
    private Range<Double> filter;
    
    public LoadGroupLoadCapacityFilter(LoadGroupService loadGroupService, Range<Double> filter) {
        this.loadGroupService = loadGroupService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> retVal =
            new ArrayList<PostProcessingFilter<ControllablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<ControllablePao>() {

            @Override
            public boolean matches(ControllablePao object) {
                return true;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
