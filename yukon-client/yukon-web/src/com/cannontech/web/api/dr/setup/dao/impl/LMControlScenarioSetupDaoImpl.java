package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private YukonJdbcTemplate jdbcTemplate;

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
        sqlTotalCountQuery.append(getFromAndWhereClause(filter));

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);
        return totalHitCount;
    }

    @Override
    public SqlStatementBuilder getFromAndWhereClause(LMSetupFilter filter) {
        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append("FROM YukonPAObject ypo");
        statementBuilder.append("LEFT OUTER JOIN LMControlScenarioProgram lmcsp");
        statementBuilder.append("ON ypo.PAObjectID = lmcsp.ScenarioID");
        statementBuilder.append("WHERE ypo.Type='LMSCENARIO'");

        if (StringUtils.isNotBlank(filter.getName())) {
            statementBuilder.append("AND ypo.PAOName").contains(filter.getName().toUpperCase());
        }

        return statementBuilder;
    }

    private static final YukonRowMapper<ControlScenarioProgram> controlScnerioRowMapper = (YukonResultSet rs) -> {
        ControlScenarioProgram liteControlScenario = new ControlScenarioProgram();
        liteControlScenario.setScenarioId(rs.getInt("PAObjectID"));
        liteControlScenario.setProgramId(rs.getNullableInt("ProgramID"));
        return liteControlScenario;
    };

    @Override
    public String getColumnNames() {
        return "PAObjectID,ProgramID";
    }

}
