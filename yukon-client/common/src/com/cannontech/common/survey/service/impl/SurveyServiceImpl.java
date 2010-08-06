package com.cannontech.common.survey.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.dao.impl.EnergyCompanyFilter;
import com.cannontech.common.survey.dao.impl.SurveyRowMapper;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;

public class SurveyServiceImpl implements SurveyService {
    private FilterService filterService;

    private final static RowMapperWithBaseQuery<Survey> rowMapper =
        new SurveyRowMapper();

    @Override
    public SearchResult<Survey> findSurveys(final int energyCompanyId,
            int startIndex, int count) {

        UiFilter<Survey> filter = new UiFilter<Survey>() {
            @Override
            public Iterable<PostProcessingFilter<Survey>> getPostProcessingFilters() {
                return null;
            }

            @Override
            public Iterable<SqlFilter> getSqlFilters() {
                List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
                retVal.add(new EnergyCompanyFilter(energyCompanyId));
                return retVal;
            }
        };

        SearchResult<Survey> retVal =
            filterService.filter(filter, null, startIndex, count, rowMapper);
        return retVal;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
