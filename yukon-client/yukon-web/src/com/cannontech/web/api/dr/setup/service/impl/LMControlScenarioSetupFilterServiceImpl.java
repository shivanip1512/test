package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMControlScenarioSetupDaoImpl;
import com.cannontech.web.api.dr.setup.model.ControlScenarioProgram;
import com.cannontech.web.api.dr.setup.model.ControlScenarioFilteredResult;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMControlScenarioSetupFilterServiceImpl implements LMSetupFilterService <ControlScenarioFilteredResult> {
    @Autowired private LMControlScenarioSetupDaoImpl setupDao;
    @Autowired private ServerDatabaseCache serverDatabaseCache;

    public SearchResults<ControlScenarioFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {

        List<ControlScenarioProgram> scenarios = setupDao.getDetails(filterCriteria);

        List<ControlScenarioFilteredResult> filteredResultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(scenarios)) {
            Map<Integer, List<ControlScenarioProgram>> scenariosMap = scenarios.stream()
                                                                               .collect(Collectors.groupingBy(ControlScenarioProgram::getScenarioId,
                                                                                                              LinkedHashMap::new,
                                                                                                              Collectors.toList()));

            for (Map.Entry<Integer, List<ControlScenarioProgram>> entry : scenariosMap.entrySet()) {
                ControlScenarioFilteredResult controlScenario = new ControlScenarioFilteredResult();

                LMDto scenario = new LMDto();
                scenario.setId(entry.getKey());
                List<ControlScenarioProgram> liteControlScenarios = entry.getValue();
                scenario.setName(getPaoName(entry.getKey()));
                controlScenario.setScenario(scenario);

                List<LMDto> assignedPrograms = new ArrayList<>();
                for (ControlScenarioProgram liteControlScenario : liteControlScenarios) {
                    if (liteControlScenario.getProgramId() != null) {
                        LMDto assignedProgram = new LMDto();
                        assignedProgram.setId(liteControlScenario.getProgramId());
                        assignedProgram.setName(getPaoName(liteControlScenario.getProgramId()));
                        assignedPrograms.add(assignedProgram);
                    }
                }
                if (CollectionUtils.isNotEmpty(assignedPrograms)) {
                    assignedPrograms.sort(Comparator.comparing(e -> e.getName(), String.CASE_INSENSITIVE_ORDER));
                    controlScenario.setAssignedPrograms(assignedPrograms);
                }

                filteredResultList.add(controlScenario);
            }
        }

        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<ControlScenarioFilteredResult> searchResults = SearchResults.pageBasedForSublist(filteredResultList,
                                                                                                       filterCriteria.getPagingParameters(),
                                                                                                       totalHitCount);

        return searchResults;
    }

    // Retrieves PAO Name from Server Database Cache
    private String getPaoName(Integer paoId) {
        return serverDatabaseCache.getAllPaosMap().get(paoId).getPaoName();
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.CONTROL_SCENARIO;
    }

}
