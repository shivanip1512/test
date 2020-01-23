package com.cannontech.web.api.dr.setup.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class LMControlAreaSetupDaoImpl extends AbstractLMSetupDaoImpl<LMDto> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    
    private final YukonRowMapper<LMDto> rowMapper = new YukonRowMapper<LMDto>() {
        @Override
        public LMDto mapRow(YukonResultSet rs) throws SQLException {
            final LMDto lmdto = new LMDto();
            lmdto.setId(rs.getInt("PAObjectId"));
            lmdto.setName(rs.getString("PAOName"));

            return lmdto;
        }
    };

    @Override
    public List<LMDto> getDetails(FilterCriteria<LMSetupFilter> criteria) {
        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc : criteria
                .getSortingParameters().getDirection());
        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? SortBy.NAME.getDbString() : criteria
                .getSortingParameters().getSort());

        SqlStatementBuilder sqlStatementBuilder = getCommonQuery(sortingDirection, sortValue, criteria.getFilteringParameters(),
                getColumnNames());
        SqlStatementBuilder sqlPaginationQuery = getPaginationQuery(criteria);
        sqlStatementBuilder.append(sqlPaginationQuery);

        final List<LMDto> resultList = jdbcTemplate.query(sqlStatementBuilder, rowMapper);

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
        statementBuilder.append("FROM LMControlArea lmca ");
        statementBuilder.append("JOIN YukonPAObject pao on lmca.DeviceID = pao.PAObjectId ");

        if (StringUtils.isNotBlank(filter.getName())) {
            statementBuilder.append("AND PAOName").contains(filter.getName());
        }
        return statementBuilder;
    }

    @Override
    public String getColumnNames() {
        return "PAObjectId, PAOName";
    }

    /**
     * Get programs assigned to a control area in sorted order.
     */
    public List<LMDto> getProgramsForControlArea(int controlAreaId) {
        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append("SELECT PAObjectID,PAOName");
        statementBuilder.append("FROM YukonPAObject ypao ");
        statementBuilder.append("JOIN LMControlAreaProgram lmcap on ypao.PAObjectID = lmcap.LMPROGRAMDEVICEID ");
        statementBuilder.append("WHERE lmcap.DeviceID").eq(controlAreaId);
        statementBuilder.append("ORDER BY PAOName");
        
        List<LMDto> programs = jdbcTemplate.query(statementBuilder, rowMapper);
        return programs;
    }
}