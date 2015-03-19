package com.cannontech.stars.dr.appliance.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
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
    
    @Autowired private FilterDao filterDao;
    @Autowired private WebConfigurationDao webConfigurationDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    
    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public SearchResults<AssignedProgram> filter(int categoryId, UiFilter<AssignedProgram> filter, 
            SortingParameters sorting, PagingParameters paging) {
        
        List<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        filters.add(new AssignedProgramForApplianceCategoryFilter(Collections.singletonList(categoryId)));
        if (filter != null) filters.add(filter);
        
        int highestProgramOrder = assignedProgramDao.getHighestProgramOrder(categoryId);
        
        return filter(filters, highestProgramOrder, sorting, paging);
    }
    
    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public SearchResults<AssignedProgram> filter(Iterable<Integer> categoryIds, UiFilter<AssignedProgram> filter, 
            SortingParameters sorting, PagingParameters paging) {
        
        List<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        if (categoryIds != null) filters.add(new AssignedProgramForApplianceCategoryFilter(categoryIds));
        if (filter != null) filters.add(filter);
        
        return filter(filters, null, sorting, paging);
    }
    
    private SearchResults<AssignedProgram> filter(List<UiFilter<AssignedProgram>> filters, Integer highestProgramOrder,
                                                 SortingParameters sorting, PagingParameters paging) {
        
        SortBy sortBy = sorting == null ? SortBy.PROGRAM_ORDER : SortBy.valueOf(sorting.getSort());
        boolean desc = sorting == null ? true : sorting.getDirection() == Direction.desc;
        
        UiFilter<AssignedProgram> filter = UiFilterList.wrap(filters);
        AssignedProgramRowMapper mapper = new AssignedProgramRowMapper(sortBy, desc, highestProgramOrder, null);
        
        SearchResults<AssignedProgram> searchResult;
        if (paging != null) {
            // With paging
            int startIndex = paging.getStartIndex();
            int count = paging.getItemsPerPage();
            searchResult = filterDao.filter(filter, null, startIndex, count, mapper);
        } else {
            // Without paging
            List<AssignedProgram> filtered = filterDao.filter(filter, null, mapper);
            searchResult = new SearchResults<>();
            searchResult.setResultList(filtered);
            searchResult.setStartIndex(0);
            searchResult.setEndIndex(filtered.size());
            searchResult.setHitCount(filtered.size());
        }
        
        List<AssignedProgram> results = searchResult.getResultList();
        
        Iterable<Integer> assignedProgramIds = Iterables.transform(results, new Function<AssignedProgram, Integer>() {
            @Override
            public Integer apply(AssignedProgram from) {
                return from.getAssignedProgramId();
            }
        });
        
        Map<Integer, WebConfiguration> webConfigurations = webConfigurationDao.getForAssignedPrograms(assignedProgramIds);
        for (AssignedProgram assignedProgram : results) {
            assignedProgram.setWebConfiguration(webConfigurations.get(assignedProgram.getWebConfigurationId()));
        }
        
        return searchResult;
    }
    
}