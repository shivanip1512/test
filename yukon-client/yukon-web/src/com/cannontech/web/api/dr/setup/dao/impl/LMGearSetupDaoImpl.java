package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.LMProgramDirectGearRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;

public class LMGearSetupDaoImpl extends AbstractLMSetupDaoImpl<LMProgramDirectGear> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<LMProgramDirectGear> getDetails(FilterCriteria<LMSetupFilter> criteria) {

        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc
                : criteria.getSortingParameters().getDirection());

        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? GearSortBy.GEARNAME.getDbString()
                : criteria.getSortingParameters().getSort());

        SqlStatementBuilder sqlStatementBuilder = getCommonQuery(sortingDirection, sortValue, criteria.getFilteringParameters(), getColumnNames());
        SqlStatementBuilder sqlPaginationQuery = getPaginationQuery(criteria);
        sqlStatementBuilder.append(sqlPaginationQuery);

        final List<LMProgramDirectGear> resultList = jdbcTemplate.query(sqlStatementBuilder, new LMProgramDirectGearRowMapper());

        return resultList;
    }

    public SqlStatementBuilder getTableAndWhereClause(LMSetupFilter filter) {

        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append("FROM LMProgramDirectGear lmdg ");
        statementBuilder.append("JOIN YukonPAObject pao on lmdg.DeviceID = pao.PAObjectId ");

        if (filter.getGearTypes() != null && !filter.getGearTypes().isEmpty()) {
            statementBuilder.append("WHERE ControlMethod").in_k(filter.getGearTypes());
        } else {
            statementBuilder.append("WHERE ControlMethod").in_k(Arrays.asList(GearControlMethod.values()));
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            statementBuilder.append("AND GearName").contains(filter.getName());
        }

        if (CollectionUtils.isNotEmpty(filter.getProgramIds())) {
            statementBuilder.append("AND DeviceID").in(filter.getProgramIds());
        }
        return statementBuilder;
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
    public String getColumnNames() {
        return "*";
    }

}
