package com.cannontech.web.amr.csr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.csr.model.FilterBy;

public class CsrUtils {

    public static List<FilterBy> getQueryFilter(HttpServletRequest request, List<FilterBy> filterByList) {
        
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
    
    public static String getFilterByString(List<FilterBy> queryFilter) {
        
        List<String> filterByStringList = new ArrayList<String>();
        for (FilterBy filterBy : queryFilter) {
            filterByStringList.add(filterBy.toCsrString());
        }
        return StringUtils.join(filterByStringList, " and ");
    }
    
}
