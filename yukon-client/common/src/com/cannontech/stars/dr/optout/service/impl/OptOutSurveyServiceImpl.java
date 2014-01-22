package com.cannontech.stars.dr.optout.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.dao.impl.OptOutSurveyRowMapper;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class OptOutSurveyServiceImpl implements OptOutSurveyService {
    @Autowired private OptOutSurveyDao optOutSurveyDao;
    @Autowired private FilterDao filterDao;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    @Autowired private AssignedProgramDao assignedProgramDao;

    private final static RowMapperWithBaseQuery<OptOutSurvey> rowMapper =
        new OptOutSurveyRowMapper();

    @Override
    @Transactional(readOnly=true)
    public Multimap<Integer, Integer> getActiveSurveyIdsByInventoryId(
            Iterable<Integer> inventoryIds) {
        Multimap<Integer, LMHardwareControlGroup> enrollmentsByInventoryId =
            lmHardwareControlGroupDao.getCurrentEnrollmentByInventoryIds(inventoryIds);

        Multimap<Integer, Integer> inventoryIdsByAssignedProgramId = ArrayListMultimap.create();

        Set<Integer> assignedProgramIds = Sets.newHashSet();
        for (LMHardwareControlGroup enrollment : enrollmentsByInventoryId.values()) {
            assignedProgramIds.add(enrollment.getProgramId());
            inventoryIdsByAssignedProgramId.put(enrollment.getProgramId(), enrollment.getInventoryId());
        }

        Map<Integer, Integer> programIdsByAssignedProgramId =
            assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIds);
        Multimap<Integer, Integer> surveyIdsByProgramId =
            optOutSurveyDao.getCurrentSurveysByProgramId(programIdsByAssignedProgramId.values());

        Multimap<Integer, Integer> surveyIdsByInventoryId = HashMultimap.create();
        for (Integer assignedProgramId : programIdsByAssignedProgramId.keySet()) {
            Integer programId = programIdsByAssignedProgramId.get(assignedProgramId);
            Collection<Integer> surveyIdsForProgram = surveyIdsByProgramId.get(programId);
            Collection<Integer> inventoryIdsForProgram =
                inventoryIdsByAssignedProgramId.get(assignedProgramId);
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
    public SearchResults<OptOutSurvey> findSurveys(final int energyCompanyId,
            int startIndex, int count) {

        UiFilter<OptOutSurvey> filter = new SqlFragmentUiFilter<OptOutSurvey>() {
            @Override
            protected void getSqlFragment(SqlBuilder sql) {
                sql.append("energyCompanyId").eq(energyCompanyId);
            }
        };

        SearchResults<OptOutSurvey> retVal =
            filterDao.filter(filter, null, startIndex, count, rowMapper);

        Multimap<OptOutSurvey, Integer> programsByOptOutSurveyId =
            optOutSurveyDao.getProgramsForOptOutSurveys(retVal.getResultList());
        for (OptOutSurvey optOutSurvey : retVal.getResultList()) {
            optOutSurvey.setProgramIds(programsByOptOutSurveyId.get(optOutSurvey));
        }

        return retVal;
    }

    @Override
    public int countAllSurveyResultsBetween(ReadableInstant begin, ReadableInstant end) {
        return optOutSurveyDao.countAllSurveyResultsBetween(begin, end);
    }
}
