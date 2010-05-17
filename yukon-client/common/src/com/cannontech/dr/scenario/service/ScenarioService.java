package com.cannontech.dr.scenario.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.user.YukonUserContext;

public interface ScenarioService {
    public SearchResult<ControllablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<ControllablePao> filter,
            Comparator<ControllablePao> sorter, int startIndex, int count);

    public List<ControllablePao> findScenariosForProgram(int programId);

    /**
     * This method will take in a list of controllablePaos and replace each
     * controllablePao that is a scenario with a scenario object that contains
     * the number of programs.  This is then used to help the UI decide if a scenario
     * can be controlled or not.
     */
    public void addScenarioActionState(List<ControllablePao> controllablePaos);

}
