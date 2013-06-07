package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.Range;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;

public class LoadGroupLastActionFilter implements UiFilter<DisplayablePao> {
    private LoadGroupService loadGroupService;

    private Range<Date> filter;

    public LoadGroupLastActionFilter(LoadGroupService loadGroupService,
            Range<Date> filter) {
        this.loadGroupService = loadGroupService;
        this.filter = filter;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilterAdapter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                DirectGroupBase group = loadGroupService.getGroupForPao(pao);
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
