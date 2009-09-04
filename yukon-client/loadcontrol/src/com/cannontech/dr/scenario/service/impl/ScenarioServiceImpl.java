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
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.model.DisplayablePaoComparator;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;

public class ScenarioServiceImpl implements ScenarioService {
    private ScenarioDao scenarioDao = null;
    private FilterService filterService;

    private static RowMapperWithBaseQuery<DisplayablePao> rowMapper = new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paObjectId, paoName FROM yukonPAObject" + " WHERE type = ");
            retVal.appendArgument(PaoType.LM_SCENARIO.getDbString());
            return retVal;
        }

        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            DisplayablePao retVal = new DisplayableDevice(paoId,
                                                          rs.getString("paoName"));
            return retVal;
        }
    };

    @Override
    public SearchResult<DisplayablePao> filterScenarios(
            YukonUserContext userContext,
            List<UiFilter<DisplayablePao>> filters,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        Comparator<DisplayablePao> secondarySorter = new DisplayablePaoComparator(userContext, false);
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filters, sorter, secondarySorter, startIndex,
                                 count, rowMapper);
        return searchResult;
    }

    @Override
    public DisplayablePao getScenario(int scenarioId) {
        return scenarioDao.getScenario(scenarioId);
    }

    @Override
    public List<DisplayablePao> findScenariosForProgram(int programId) {
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
