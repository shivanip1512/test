package com.cannontech.dr.scenario.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.user.YukonUserContext;

public interface ScenarioService {
    public SearchResults<DisplayablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

    public Scenario getScenario(int scenarioId);
    
    public List<Scenario> findScenariosForProgram(int programId);

}