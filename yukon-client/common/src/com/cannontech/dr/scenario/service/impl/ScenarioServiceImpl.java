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
            retVal.append("SELECT PAO.PAObjectId, PAO.PAOName, COUNT(LMCSP.ProgramId) ProgramCount");    
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
            Scenario retVal = new Scenario(paoId,
                                           rs.getString("paoName"));
            retVal.setProgramCount(rs.getInt("ProgramCount"));
            
            return retVal;
        }
    };

    @Override
    public SearchResult<ControllablePao> filterScenarios(
            YukonUserContext userContext, UiFilter<ControllablePao> filter,
            Comparator<ControllablePao> sorter, int startIndex, int count) {

        SearchResult<ControllablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);

        return searchResult;
    }

    @Override
    public void addScenarioActionState(List<ControllablePao> controllablePaos) {
        for (int i = 0; i < controllablePaos.size(); i++) {
            ControllablePao controllablePao = controllablePaos.get(i);
            if (controllablePao.getPaoIdentifier().getPaoType().equals(PaoType.LM_SCENARIO)) {
                Scenario scenario = 
                    scenarioDao.getScenario(controllablePao.getPaoIdentifier().getPaoId());
                controllablePaos.set(i, scenario);
            }
        }
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
