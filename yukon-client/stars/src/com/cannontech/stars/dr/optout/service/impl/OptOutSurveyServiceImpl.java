package com.cannontech.stars.dr.optout.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.dao.impl.OptOutSurveyRowMapper;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class OptOutSurveyServiceImpl implements OptOutSurveyService {
    private OptOutSurveyDao optOutSurveyDao;
    private FilterService filterService;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;

    private final static RowMapperWithBaseQuery<OptOutSurvey> rowMapper =
        new OptOutSurveyRowMapper();

    @Override
    @Transactional(readOnly=true)
    public Multimap<Integer, Integer> getActiveSurveyIdsByInventoryId(
            Iterable<Integer> inventoryIds) {
        Multimap<Integer, LMHardwareControlGroup> enrollmentsByInventoryId =
            lmHardwareControlGroupDao.getCurrentEnrollmentByInventoryIds(inventoryIds);

        Multimap<Integer, Integer> inventoryIdsByProgramId = ArrayListMultimap.create();

        Set<Integer> programIds = Sets.newHashSet();
        for (LMHardwareControlGroup enrollment : enrollmentsByInventoryId.values()) {
            programIds.add(enrollment.getProgramId());
            inventoryIdsByProgramId.put(enrollment.getProgramId(), enrollment.getInventoryId());
        }

        Multimap<Integer, Integer> surveyIdsByProgramId = optOutSurveyDao.getCurrentSurveysByProgramId(programIds);

        Multimap<Integer, Integer> surveyIdsByInventoryId = HashMultimap.create();
        for (Integer programId : surveyIdsByProgramId.keySet()) {
            Collection<Integer> surveyIdsForProgram = surveyIdsByProgramId.get(programId);
            Collection<Integer> inventoryIdsForProgram = inventoryIdsByProgramId.get(programId);
            for (Integer surveyId : surveyIdsForProgram) {
                for (Integer inventoryId : inventoryIdsForProgram) {
                    surveyIdsByInventoryId.put(inventoryId, surveyId);
                }
            }
        }

        return surveyIdsByInventoryId;
    }

    @Override
    @Transactional(readOnly=true)
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

        Multimap<OptOutSurvey, Integer> programsByOptOutSurveyId =
            optOutSurveyDao.getProgramsForOptOutSurveys(retVal.getResultList());
        for (OptOutSurvey optOutSurvey : retVal.getResultList()) {
            optOutSurvey.setProgramIds(Lists.newArrayList(programsByOptOutSurveyId.get(optOutSurvey)));
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

    @Autowired
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
}
