package com.cannontech.stars.dr.appliance.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramForApplianceCategoryFilter;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.AssignedProgramService;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.cannontech.user.YukonUserContext;

public class AssignedProgramServiceImpl implements AssignedProgramService {
    private FilterService filterService;
    private WebConfigurationDao webConfigurationDao;
    private AssignedProgramDao assignedProgramDao;

    @Override
    @Transactional(readOnly=true)
    public SearchResult<AssignedProgram> filter(int applianceCategoryId,
            UiFilter<AssignedProgram> filter, boolean sortByName,
            boolean sortDescending, int startIndex, int count,
            YukonUserContext userContext) {
        Map<Integer, WebConfiguration> webConfigurations =
            webConfigurationDao.getForProgramsForApplianceCateogry(applianceCategoryId);

        List<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        filters.add(new AssignedProgramForApplianceCategoryFilter(applianceCategoryId));
        filters.add(filter);

        int highestProgramOrder =
            assignedProgramDao.getHighestProgramOrderForApplianceCategory(applianceCategoryId);
        RowMapperWithBaseQuery<AssignedProgram> rowMapper =
            new AssignedProgramRowMapper(sortByName, sortDescending,    
                                         highestProgramOrder, webConfigurations);
        SearchResult<AssignedProgram> searchResult =
            filterService.filter(UiFilterList.wrap(filters), null, startIndex,
                                 count, rowMapper);

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
