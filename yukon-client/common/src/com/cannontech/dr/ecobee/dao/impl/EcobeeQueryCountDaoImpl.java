package com.cannontech.dr.ecobee.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCount;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;

public class EcobeeQueryCountDaoImpl implements EcobeeQueryCountDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public void incrementQueryCount(EcobeeQueryType queryType) {
        DateTime now = new DateTime();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE EcobeeQueryStatistics");
        sql.append("SET QueryCount = QueryCount + 1");
        sql.append("WHERE MonthIndex").eq_k(now.getMonthOfYear());
        sql.append("AND YearIndex").eq_k(now.getYear());
        sql.append("AND QueryType").eq_k(queryType);
        int rowsAffected = jdbcTemplate.update(sql);

        //No update means we need a new row. Insert one.
        if (rowsAffected == 0) {
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO EcobeeQueryStatistics");
            insertSql.append("(MonthIndex, YearIndex, QueryType, QueryCount)");
            insertSql.values(now.getMonthOfYear(), now.getYear(), queryType, 1);
            jdbcTemplate.update(insertSql);
        }
    }

    @Override
    public EcobeeQueryStatistics getCountsForMonth(MonthYear monthYear) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT QueryType, QueryCount");
        sql.append("FROM EcobeeQueryStatistics");
        sql.append("WHERE YearIndex").eq_k(monthYear.getYear());
        sql.append("AND MonthIndex").eq_k(monthYear.getMonth());

        final EcobeeQueryStatistics statistics = new EcobeeQueryStatistics(monthYear.getYear(), monthYear.getMonth());
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                EcobeeQueryType queryType = rs.getEnum("QueryType", EcobeeQueryType.class);
                int count = rs.getInt("QueryCount");
                EcobeeQueryCount queryCount = new EcobeeQueryCount(queryType, count);
                statistics.addQueryCount(queryCount);
            }
        });
        return statistics;
    }

    @Override
    public List<EcobeeQueryStatistics> getCountsForRange(Range<MonthYear> dateRange) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonthIndex, YearIndex, QueryType, QueryCount");
        sql.append("FROM EcobeeQueryStatistics");
        sql.append("WHERE (");
        appendMonthYearClauses(sql, dateRange);
        sql.append(")");

        final List<EcobeeQueryStatistics> statisticsList = new ArrayList<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int monthIndex = rs.getInt("MonthIndex");
                int yearIndex = rs.getInt("YearIndex");
                EcobeeQueryStatistics statistics = new EcobeeQueryStatistics(yearIndex, monthIndex);

                EcobeeQueryType queryType = rs.getEnum("QueryType", EcobeeQueryType.class);
                int count = rs.getInt("QueryCount");
                EcobeeQueryCount queryCount = new EcobeeQueryCount(queryType, count);

                int index = statisticsList.indexOf(statistics);
                if (index >= 0) {
                    statisticsList.get(index).addQueryCount(queryCount);
                } else {
                    statistics.addQueryCount(queryCount);
                    statisticsList.add(statistics);
                }
            }
        });

        return statisticsList;
    }

    /**
     * Appends clauses capturing the months in the first year and last year, and the full year for any years in between.
     */
    private void appendMonthYearClauses(SqlStatementBuilder sql, Range<MonthYear> dateRange) {
        int startYear = dateRange.getMin().getYear();
        int startMonth = dateRange.getMin().getMonth();
        int endYear = dateRange.getMax().getYear();
        int endMonth = dateRange.getMax().getMonth();

        SqlStatementBuilder monthMinClause = new SqlStatementBuilder();
        if (dateRange.isIncludesMaxValue()) {
            monthMinClause.append("AND MonthIndex").gte(startMonth);
        } else {
            monthMinClause.append("AND MonthIndex").gt(startMonth);
        }

        SqlStatementBuilder monthMaxClause = new SqlStatementBuilder();
        if(dateRange.isIncludesMinValue()) {
            monthMaxClause.append("AND MonthIndex").lte(endMonth);
        } else {
            monthMaxClause.append("AND MonthIndex").lt(endMonth);
        }

        if (startYear == endYear) {
            sql.append("(");
            sql.append("YearIndex").eq_k(startYear);
            sql.append(monthMinClause);
            sql.append(monthMaxClause);
            sql.append(")");
        } else {
            sql.append("(");
            sql.append("YearIndex").eq_k(startYear);
            sql.append(monthMinClause);
            sql.append(")");

            appendMidYearsClause(sql, startYear, endYear);

            sql.append("OR (");
            sql.append("YearIndex").eq_k(endYear);
            sql.append(monthMaxClause);
            sql.append(")");
        }
    }

    /**
     * Appends a clause for handling any years between the first and last year, if the range covers 3 or more years.
     */
    private void appendMidYearsClause(SqlStatementBuilder sql, int startYear, int endYear) {
        if (endYear > startYear + 1) {
            // Handle years in between where months don't matter.
            sql.append("OR (");
            sql.append("YearIndex").gt(startYear);
            sql.append("AND YearIndex").lt(endYear);
            sql.append(")");
        }
    }
}
