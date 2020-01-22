package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.cannontech.web.api.dr.setup.model.ControlScenariosFilteredResult;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMControlScenarioSetupFilterServiceImpl implements LMSetupFilterService <ControlScenariosFilteredResult> {
    @Autowired private LMControlScenarioSetupDaoImpl setupDao;
    @Autowired private ServerDatabaseCache serverDatabaseCache;

    public SearchResults<ControlScenariosFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {

        List<ControlScenarioProgram> scenarios = setupDao.getDetails(filterCriteria);
        List<ControlScenariosFilteredResult> filteredResultList = new ArrayList<>();
        Map<Integer, List<ControlScenarioProgram>> scenariosMap = scenarios.stream()
                                                                        .collect(Collectors.groupingBy(ControlScenarioProgram::getScenarioId,
                                                                                                      LinkedHashMap::new,
                                                                                                      Collectors.toList()));

        for (Map.Entry<Integer, List<ControlScenarioProgram>> entry : scenariosMap.entrySet()) {
            ControlScenariosFilteredResult controlScenario = new ControlScenariosFilteredResult();

            LMDto scenario = new LMDto();
            scenario.setId(entry.getKey());
            List<ControlScenarioProgram> liteControlScenarios = entry.getValue();
            scenario.setName(liteControlScenarios.get(0).getScenarioName());
            controlScenario.setScenario(scenario);

            List<LMDto> assignedPrograms = new ArrayList<>();
            for (ControlScenarioProgram liteControlScenario : liteControlScenarios) {
                LMDto assignedProgram = new LMDto();
                assignedProgram.setId(liteControlScenario.getProgramID());
                assignedProgram.setName(serverDatabaseCache.getAllPaosMap().get(liteControlScenario.getProgramID()).getPaoName());
                assignedPrograms.add(assignedProgram);
            }

            assignedPrograms.sort(Comparator.comparing(e -> e.getName()));

            controlScenario.setAssignedPrograms(assignedPrograms);

            filteredResultList.add(controlScenario);
        }

        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<ControlScenariosFilteredResult> searchResults = SearchResults.pageBasedForSublist(filteredResultList,
                                                                                                        filterCriteria.getPagingParameters(),
                                                                                                        totalHitCount);
        return searchResults;
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.CONTROL_SCENARIO;
    }

}
