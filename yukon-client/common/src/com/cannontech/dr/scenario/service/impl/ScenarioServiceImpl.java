package com.cannontech.dr.scenario.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;

public class ScenarioServiceImpl implements ScenarioService {
    private ScenarioDao scenarioDao;
    private FilterService filterService;

    private static RowMapperWithBaseQuery<ControllablePao> rowMapper = new AbstractRowMapperWithBaseQuery<ControllablePao>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT PAO.PAObjectId, PAO.PAOName, COUNT(LMCSP.ProgramId) ScenarioProgramCount");    
            retVal.append("FROM YukonPAObject PAO");                                                     
            retVal.append("LEFT JOIN LMControlScenarioProgram LMCSP ON LMCSP.ScenarioId = PAO.PAObjectId ");
            retVal.append("WHERE PAO.Type").eq(PaoType.LM_SCENARIO.getDatabaseRepresentation());   
            return retVal;
        }

        @Override
        public SqlStatementBuilder getGroupBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("GROUP BY PAO.PAObjectId, PAO.PAOName");
            return retVal;
        }
        
        @Override
        public ControllablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            String paoName = rs.getString("paoName");
            int scenarioProgramCount = rs.getInt("ScenarioProgramCount");
            return new Scenario(paoId, paoName, scenarioProgramCount);
        }
    };

    @Override
    public SearchResult<ControllablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<ControllablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);

        return searchResult;
    }

    @Override
    public List<ControllablePao> findScenariosForProgram(int programId) {
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
