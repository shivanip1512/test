package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.Range;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;

public class LoadGroupLastActionFilter implements UiFilter<ControllablePao> {
    private LoadGroupService loadGroupService;

    private Range<Date> filter;

    public LoadGroupLastActionFilter(LoadGroupService loadGroupService,
            Range<Date> filter) {
        this.loadGroupService = loadGroupService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<ControllablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<ControllablePao>> retVal =
            new ArrayList<PostProcessingFilter<ControllablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<ControllablePao>() {

            @Override
            public boolean matches(ControllablePao pao) {
                LMDirectGroupBase group = loadGroupService.getGroupForPao(pao);
                boolean retVal = group != null && filter.intersects(group.getGroupTime());
                return retVal;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
