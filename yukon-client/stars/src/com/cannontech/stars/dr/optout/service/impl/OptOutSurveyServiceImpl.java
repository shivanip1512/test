package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.dao.impl.OptOutSurveyRowMapper;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class OptOutSurveyServiceImpl implements OptOutSurveyService {
    private OptOutSurveyDao optOutSurveyDao;
    private FilterService filterService;

    private final static RowMapperWithBaseQuery<OptOutSurvey> rowMapper =
        new OptOutSurveyRowMapper();

    @Override
    public SearchResult<OptOutSurvey> findSurveys(final int energyCompanyId,
            int startIndex, int count) {

        UiFilter<OptOutSurvey> filter = new UiFilter<OptOutSurvey>() {
            @Override
            public Iterable<PostProcessingFilter<OptOutSurvey>> getPostProcessingFilters() {
                return null;
            }

            @Override
            public Iterable<SqlFilter> getSqlFilters() {
                List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
                retVal.add(new SqlFilter() {
                    @Override
                    public SqlFragmentSource getWhereClauseFragment() {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("energyCompanyId").eq(energyCompanyId);
                        return sql;
                    }
                });
                return retVal;
            }
        };

        SearchResult<OptOutSurvey> retVal =
            filterService.filter(filter, null, startIndex, count, rowMapper);

        Multimap<OptOutSurvey, Integer> loginGroupsByOptOutSurveyId =
            optOutSurveyDao.getLoginGroupsForOptOutSurveys(retVal.getResultList());
        for (OptOutSurvey optOutSurvey : retVal.getResultList()) {
            optOutSurvey.setLoginGroupIds(Lists.newArrayList(loginGroupsByOptOutSurveyId.get(optOutSurvey)));
        }

        return retVal;
    }

    @Autowired
    public void setOptOutSurveyDao(OptOutSurveyDao optOutSurveyDao) {
        this.optOutSurveyDao = optOutSurveyDao;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
