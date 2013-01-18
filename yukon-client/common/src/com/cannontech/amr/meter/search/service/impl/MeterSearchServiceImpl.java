package com.cannontech.amr.meter.search.service.impl;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.common.search.SearchResult;

public class MeterSearchServiceImpl implements MeterSearchService {

    private MeterSearchDao searchDao = null;

    public void setMeterSearchDao(MeterSearchDao meterSearchDao) {
        this.searchDao = meterSearchDao;
    }

    public SearchResult<Meter> search(List<FilterBy> filterByList, MeterSearchOrderBy orderBy, int start,
            int count) {

        return searchDao.search(filterByList, orderBy, start, count);
    }

}
