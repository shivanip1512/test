package com.cannontech.dr.scenario.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.user.YukonUserContext;

public interface ScenarioService {
    public SearchResult<ControllablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<ControllablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

    public List<ControllablePao> findScenariosForProgram(int programId);

}
