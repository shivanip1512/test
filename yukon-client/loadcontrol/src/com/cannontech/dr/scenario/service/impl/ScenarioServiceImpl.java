package com.cannontech.dr.scenario.service.impl;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;

public class ScenarioServiceImpl implements ScenarioService {
    private ScenarioDao scenarioDao;
    private FilterService filterService;

    private static RowMapperWithBaseQuery<DisplayablePao> rowMapper = new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT PAO.PAObjectId, PAO.PAOName");
            retVal.append("FROM YukonPAObject PAO");
            retVal.append("WHERE PAO.Type").eq(PaoType.LM_SCENARIO);
            return retVal;
        }

        @Override
        public DisplayablePao mapRow(YukonResultSet rs)
                throws SQLException {
            PaoIdentifier paoId = 
            	new PaoIdentifier(rs.getInt("paObjectId"), PaoType.LM_SCENARIO);
            DisplayablePao retVal = 
            	new DisplayablePaoBase(paoId, rs.getString("paoName"));
            return retVal;
        }
    };

    @Override
    public SearchResult<DisplayablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);

        return searchResult;
    }

    @Override
    public Scenario getScenario(int scenarioId) {
        return scenarioDao.getScenario(scenarioId);
    }
    
    @Override
    public List<Scenario> findScenariosForProgram(int programId) {
        return scenarioDao.findScenariosForProgram(programId);
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
