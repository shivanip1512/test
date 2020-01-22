package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.api.dr.setup.model.ControlScenarioProgram;

public class LMControlScenarioSetupDaoImpl extends AbstractLMSetupDaoImpl<ControlScenarioProgram> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<ControlScenarioProgram> getDetails(FilterCriteria<LMSetupFilter> criteria) {

        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc
                : criteria.getSortingParameters().getDirection());

        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? SortBy.NAME.getDbString() : criteria.getSortingParameters().getSort());

        SqlStatementBuilder sqlStatementBuilder = getCommonQuery(sortingDirection, sortValue, criteria.getFilteringParameters(), getColumnNames());
        SqlStatementBuilder sqlPaginationQuery = getPaginationQuery(criteria);
        sqlStatementBuilder.append(sqlPaginationQuery);

        final List<ControlScenarioProgram> resultList = jdbcTemplate.query(sqlStatementBuilder, controlScnerioRowMapper);

        return resultList;
    }

    @Override
    public Integer getTotalCount(FilterCriteria<LMSetupFilter> criteria) {
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();
        LMSetupFilter filter = criteria.getFilteringParameters();

        sqlTotalCountQuery.append("SELECT COUNT(*) ");
        sqlTotalCountQuery.append(getTableAndWhereClause(filter));

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);
        return totalHitCount;
    }

    @Override
    public SqlStatementBuilder getTableAndWhereClause(LMSetupFilter filter) {
        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append(" FROM YukonPAObject ypo  ");
        statementBuilder.append("JOIN LMControlScenarioProgram lmcsp ON ypo.PAObjectID = lmcsp.ScenarioID ");

        if (filter.getName() != null && !filter.getName().isBlank()) {
            statementBuilder.append("WHERE ypo.PAOName").contains(filter.getName());
        }

        return statementBuilder;
    }

    private static final YukonRowMapper<ControlScenarioProgram> controlScnerioRowMapper = (YukonResultSet rs) -> {
        ControlScenarioProgram liteControlScenario = new ControlScenarioProgram();
        liteControlScenario.setScenarioId(rs.getInt("PAObjectID"));
        liteControlScenario.setScenarioName(rs.getString("PAOName"));
        liteControlScenario.setProgramID(rs.getInt("ProgramID"));
        return liteControlScenario;
    };

    @Override
    public String getColumnNames() {
        return "*";
    }

}
