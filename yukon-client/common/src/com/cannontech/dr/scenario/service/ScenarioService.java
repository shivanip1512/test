package com.cannontech.dr.scenario.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.user.YukonUserContext;

public interface ScenarioService {
    public SearchResult<DisplayablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

    public DisplayablePao getScenario(int scenarioId);
    public List<DisplayablePao> findScenariosForProgram(int programId);
}
