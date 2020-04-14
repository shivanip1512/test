package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.program.setup.model.ProgramConstraint;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.api.dr.setup.model.LoadProgramFilteredResult;

public class LMLoadProgramSetupDaoImpl extends AbstractLMSetupDaoImpl<LoadProgramFilteredResult> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<LoadProgramFilteredResult> getDetails(FilterCriteria<LMSetupFilter> criteria) {

        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc : criteria
                .getSortingParameters().getDirection());

        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? LoadProgramSortBy.PROGRAMNAME
                .getDbString() : criteria.getSortingParameters().getSort());

        SqlStatementBuilder sqlStatementBuilder = getCommonQuery(sortingDirection, sortValue, criteria.getFilteringParameters(),
                getColumnNames());
        SqlStatementBuilder sqlPaginationQuery = getPaginationQuery(criteria);
        sqlStatementBuilder.append(sqlPaginationQuery);
        List<LoadProgramFilteredResult> resultList = jdbcTemplate.query(sqlStatementBuilder, loadProgramRowMapper);
        return resultList;
    }

    private static final YukonRowMapper<LoadProgramFilteredResult> loadProgramRowMapper = (YukonResultSet rs) -> {
        LoadProgramFilteredResult result = new LoadProgramFilteredResult();
        result.setProgram(new LMDto(rs.getInt("PAObjectID"), rs.getString("PAOName")));
        result.setType(rs.getEnum("Type", PaoType.class));
        result.setOperationalState(rs.getEnum("ControlType", OperationalState.class));
        ProgramConstraint constraint = new ProgramConstraint();
        constraint.setConstraintId(rs.getInt("ConstraintID"));
        constraint.setConstraintName(rs.getString("ConstraintName"));
        result.setConstraint(constraint);
        return result;
    };

    @Override
    public Integer getTotalCount(FilterCriteria<LMSetupFilter> criteria) {
        LMSetupFilter filter = criteria.getFilteringParameters();
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();

        sqlTotalCountQuery.append("SELECT COUNT(*)");
        sqlTotalCountQuery.append(getFromAndWhereClause(filter));

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);
        return totalHitCount;
    }

    @Override
    public SqlStatementBuilder getFromAndWhereClause(LMSetupFilter filter) {

        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append("FROM YukonPAObject ypo JOIN LMPROGRAM lp");
        statementBuilder.append("ON ypo.PAObjectID = lp.DeviceID");
        statementBuilder.append("JOIN LMProgramConstraints lpc");
        statementBuilder.append("ON lpc.ConstraintID = lp.ConstraintID");

        if (CollectionUtils.isNotEmpty(filter.getTypes())) {
            statementBuilder.append("WHERE ypo.Type").in_k(filter.getTypes());
        } else {
            statementBuilder.append("WHERE ypo.Type").in_k(PaoType.getDirectLMProgramTypes());
        }
        if (CollectionUtils.isNotEmpty(filter.getOperationalStates())) {
            statementBuilder.append("AND lp.ControlType").in_k(filter.getOperationalStates());
        } else {
            statementBuilder.append("AND lp.ControlType").in_k(Arrays.asList(OperationalState.values()));
        }
        if (StringUtils.isNotEmpty(filter.getName())) {
            statementBuilder.append("AND UPPER(ypo.PAOName)").contains(filter.getName().toUpperCase());
        }
        return statementBuilder;
    }

    /**
     * Returns list of Gears associated with the Load Program ordered on basis of GearNumber.
     * 
     */
    public List<LMDto> getGearsOrderByGearNumber(Integer programId) {

        SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
        sqlStatementBuilder.append("SELECT GearID, GearName from LMProgramDirectGear");
        sqlStatementBuilder.append("WHERE DeviceID").eq(programId);
        sqlStatementBuilder.append("ORDER BY GearNumber");
        return jdbcTemplate.query(sqlStatementBuilder, gearMapper);
    }

    /**
     * Returns list of Load Groups associated with the Load Program ordered on basis of GroupOrder.
     * 
     */
    public List<LMDto> getLoadGroupsOrderByGroupOrder(Integer programId) {
        SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
        sqlStatementBuilder.append("SELECT lpdg.LMGroupDeviceID, ypo.PAOName");
        sqlStatementBuilder.append("FROM YukonPAObject ypo JOIN LMProgramDirectGroup lpdg");
        sqlStatementBuilder.append("ON ypo.PAObjectID = lpdg.LMGroupDeviceID");
        sqlStatementBuilder.append("WHERE lpdg.DeviceID").eq(programId);
        sqlStatementBuilder.append("ORDER BY lpdg.GroupOrder");
        return jdbcTemplate.query(sqlStatementBuilder, loadGroupMapper);
    }

    private static final YukonRowMapper<LMDto> gearMapper = (YukonResultSet rs) -> {
        return new LMDto(rs.getInt("GearID"), rs.getString("GearName"));
    };
    
    private static final YukonRowMapper<LMDto> loadGroupMapper = (YukonResultSet rs) -> {
        return new LMDto(rs.getInt("LMGroupDeviceID"), rs.getString("PAOName"));
    };

    @Override
    public String getColumnNames() {
        return "ypo.PAObjectID, ypo.PAOName, ypo.Type, lp.ControlType, lp.ConstraintID, lpc.ConstraintName";
    }
}
