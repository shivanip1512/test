package com.cannontech.web.api.dr.setup.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.DayOfWeek;
import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class LMProgramConstraintSetupDaoImpl extends AbstractLMSetupDaoImpl<ProgramConstraint> {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<ProgramConstraint> getDetails(FilterCriteria<LMSetupFilter> criteria) {
        Direction sortingDirection = ((criteria.getSortingParameters().getDirection() == null) ? Direction.asc
                : criteria.getSortingParameters().getDirection());

        String sortValue = ((criteria.getSortingParameters().getSort() == null) ? ProgramConstraintSortBy.CONSTRAINTNAME.getDbString() : criteria.getSortingParameters().getSort());

        SqlStatementBuilder sqlStatementBuilder = getCommonQuery(sortingDirection, sortValue, criteria.getFilteringParameters(), getColumnNames());
        SqlStatementBuilder sqlPaginationQuery = getPaginationQuery(criteria);
        sqlStatementBuilder.append(sqlPaginationQuery);

        final List<ProgramConstraint> resultList = jdbcTemplate.query(sqlStatementBuilder, programConstraintRowMapper);

        return resultList;
    }

    private static final YukonRowMapper<ProgramConstraint> programConstraintRowMapper = (YukonResultSet rs) -> {
        ProgramConstraint constraint = new ProgramConstraint();

        constraint.setName(rs.getString("ConstraintName"));
        constraint.setId(rs.getInt("ConstraintID"));

        String availableWeekdays = rs.getString("availableWeekdays");
        constraint.setDaySelection(DayOfWeek.buildModelRepresentation(availableWeekdays.substring(0, 7)));
        constraint.setHolidayUsage(HolidayUsage.getForHoliday(availableWeekdays.charAt(7)));

        constraint.setMaxHoursDaily((rs.getInt("maxHoursDaily")) / 3600);
        constraint.setMaxHoursMonthly((rs.getInt("maxHoursMonthly")) / 3600);
        constraint.setMaxHoursSeasonal((rs.getInt("maxHoursSeasonal")) / 3600);
        constraint.setMaxHoursAnnually((rs.getInt("maxHoursAnnually")) / 3600);

        constraint.setMinActivateSeconds(rs.getInt("minActivateTime"));
        constraint.setMinRestartSeconds(rs.getInt("minRestartTime"));
        constraint.setMaxDailyOps(rs.getInt("maxDailyOps"));
        constraint.setMaxActivateSeconds(rs.getInt("maxActivateTime"));

        LMDto seasonSchedule = new LMDto();
        seasonSchedule.setId(rs.getInt("seasonScheduleID"));
        constraint.setSeasonSchedule(seasonSchedule);

        LMDto holidaySchedule = new LMDto();
        holidaySchedule.setId(rs.getInt("holidayScheduleID"));
        constraint.setHolidaySchedule(holidaySchedule);

        return constraint;
    };

    @Override
    public Integer getTotalCount(FilterCriteria<LMSetupFilter> criteria) {
        LMSetupFilter filter = criteria.getFilteringParameters();
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();

        sqlTotalCountQuery.append("SELECT COUNT(*)");
        sqlTotalCountQuery.append(getTableAndWhereClause(filter));

        int totalHitCount = jdbcTemplate.queryForInt(sqlTotalCountQuery);
        return totalHitCount;
    }

    @Override
    public SqlStatementBuilder getTableAndWhereClause(LMSetupFilter filter) {

        SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
        statementBuilder.append("FROM LMProgramConstraints");

        if (filter.getName() != null && !filter.getName().isBlank()) {
            statementBuilder.append("WHERE ConstraintName").contains(filter.getName());
        }

        return statementBuilder;
    }

    @Override
    public String getColumnNames() {
        return "*";
    }
}
