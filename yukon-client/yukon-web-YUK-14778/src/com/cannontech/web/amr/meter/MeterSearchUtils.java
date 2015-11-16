package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.meter.search.model.FilterBy;

public class MeterSearchUtils {

    public static List<FilterBy> getQueryFilter(HttpServletRequest request, List<? extends FilterBy> filterByList) {
        
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();
        for (FilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, filterBy.getName(), "").trim();
            if (!StringUtils.isBlank(filterValue)) {
                filterBy.setFilterValue(filterValue);
                queryFilter.add(filterBy);
            }
        }
        
        return queryFilter;
    }
}
