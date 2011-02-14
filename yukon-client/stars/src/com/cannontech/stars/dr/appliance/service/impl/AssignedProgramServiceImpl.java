package com.cannontech.stars.dr.appliance.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramForApplianceCategoryFilter;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper.SortBy;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.AssignedProgramService;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class AssignedProgramServiceImpl implements AssignedProgramService {
    private FilterService filterService;
    private WebConfigurationDao webConfigurationDao;
    private AssignedProgramDao assignedProgramDao;

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public SearchResult<AssignedProgram> filter(int applianceCategoryId,
            UiFilter<AssignedProgram> filter, SortBy sortBy,
            boolean sortDescending, int startIndex, int count) {
        List<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        filters.add(new AssignedProgramForApplianceCategoryFilter(Collections.singletonList(applianceCategoryId)));
        if (filter != null) {
            filters.add(filter);
        }

        Integer highestProgramOrder =
            assignedProgramDao.getHighestProgramOrderForApplianceCategory(applianceCategoryId);
        return filter(filters, sortBy, highestProgramOrder, sortDescending, startIndex, count);
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public SearchResult<AssignedProgram> filter(Iterable<Integer> applianceCategoryIds,
                                                UiFilter<AssignedProgram> filter, SortBy sortBy,
                                                boolean sortDescending, int startIndex, int count) {
        List<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        if (applianceCategoryIds != null) {
            filters.add(new AssignedProgramForApplianceCategoryFilter(applianceCategoryIds));
        }
        if (filter != null) {
            filters.add(filter);
        }
        
        return filter(filters, sortBy, null, sortDescending, startIndex, count);
    }
    
    private SearchResult<AssignedProgram> filter(List<UiFilter<AssignedProgram>> filters,
                                                 SortBy sortBy, Integer highestProgramOrder,
                                                 boolean sortDescending, int startIndex, int count) {
        RowMapperWithBaseQuery<AssignedProgram> rowMapper =
            new AssignedProgramRowMapper(sortBy, sortDescending, highestProgramOrder, null);
        SearchResult<AssignedProgram> searchResult =
            filterService.filter(UiFilterList.wrap(filters), null, startIndex, count, rowMapper);

        Function<AssignedProgram, Integer> idFromAssignedProgram = new Function<AssignedProgram, Integer>() {
            @Override
            public Integer apply(AssignedProgram from) {
                return from.getAssignedProgramId();
            }
        };

        Iterable<Integer> assignedProgramIds =
            Iterables.transform(searchResult.getResultList(), idFromAssignedProgram);
        Map<Integer, WebConfiguration> webConfigurations =
            webConfigurationDao.getForAssignedPrograms(assignedProgramIds);
        for (AssignedProgram assignedProgram : searchResult.getResultList()) {
            assignedProgram.setWebConfiguration(webConfigurations.get(assignedProgram.getWebConfigurationId()));
        }

        return searchResult;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setWebConfigurationDao(WebConfigurationDao webConfigurationDao) {
        this.webConfigurationDao = webConfigurationDao;
    }

    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }
}
