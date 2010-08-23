package com.cannontech.common.survey.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.survey.dao.impl.SurveyRowMapper;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.util.SqlBuilder;

public class SurveyServiceImpl implements SurveyService {
    private FilterService filterService;

    private final static RowMapperWithBaseQuery<Survey> rowMapper =
        new SurveyRowMapper();

    @Override
    public SearchResult<Survey> findSurveys(final int energyCompanyId,
            int startIndex, int count) {

        UiFilter<Survey> filter = new SqlFragmentUiFilter<Survey>() {
            @Override
            protected void getSqlFragment(SqlBuilder sql) {
                sql.append("energyCompanyId").eq(energyCompanyId);
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
