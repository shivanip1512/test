package com.cannontech.amr.meter.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;

public class MeterSearchServiceImpl implements MeterSearchService {

    @Autowired private MeterSearchDao searchDao;
    
    @Override
    public SearchResults<YukonMeter> search(List<FilterBy> filters, MeterSearchOrderBy orderBy, PagingParameters paging) {
        return searchDao.search(filters, orderBy, paging.getStartIndex(), paging.getItemsPerPage());
    }
    
}