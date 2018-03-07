package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.YukonJdbcTemplate;


public class StrategyAssignmentModel extends BareReportModelBase<StrategyAssignmentModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    //inputs
    private String selectedStrategy;
    
    // member variables
    private final List<ModelRow> data = new ArrayList<ModelRow>();
    private final String SEASON_SPLIT_FIRST_HALF = "&1&";

    private List<Integer> strategyIds;

    private SystemDateFormattingService systemDateFormattingService;
    
    public StrategyAssignmentModel() {
    }
    
    static public class ModelRow {
        public String strategyName;
        public String paoName;
        public String type;
        public String controlMethod;
        public String seasonName;
        public String seasonStartDate;
        public String seasonEndDate;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    @Override
    public String getTitle() {
        return "Strategy Assignment Report";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public void doLoadData() {

        String sql = buildSQLStatement();

        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                StrategyAssignmentModel.ModelRow row = new StrategyAssignmentModel.ModelRow();
                row.strategyName = rs.getString("strategyName");
                row.paoName = rs.getString("paoName");
                row.type = rs.getString("type");
                row.controlMethod = rs.getString("controlMethod");
                row.seasonName = rs.getString("seasonName");
                LocalDate today =
                    new LocalDate(DateTimeZone.forTimeZone(systemDateFormattingService.getSystemTimeZone()));
                LocalDate startDate =
                    getLocalDate(rs.getString("seasonstartmonth"), rs.getString("seasonstartday"), today);
                LocalDate endDate = getLocalDate(rs.getString("seasonendmonth"), rs.getString("seasonendday"), today);
                if (row.seasonName.startsWith(SEASON_SPLIT_FIRST_HALF)) {
                    row.seasonName = row.seasonName.replaceFirst(SEASON_SPLIT_FIRST_HALF, "");
                    startDate = startDate.minusYears(1);
                    row.seasonStartDate = startDate.toString();
                    row.seasonEndDate = endDate.toString();
                    data.add(row);
                } else {
                    row.seasonStartDate = startDate.toString();
                    row.seasonEndDate = endDate.toString();
                    data.add(row);
                }
            }

            private LocalDate getLocalDate(String month, String day, LocalDate today) {
                LocalDate localDate = today;
                localDate = localDate.withMonthOfYear(Integer.parseInt(month));
                localDate = localDate.withDayOfMonth(Integer.parseInt(day));
                return localDate;
            }
        });
            
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public String buildSQLStatement() {
        String query = " SELECT ";
        query += "yp.PaObjectId, ";
        query += "strat.StrategyName, ";
        query += "yp.PaoName, ";
        query += "yp.Type, ";
        query += "strat.ControlMethod, ";
        query += "sa.SeasonName, ";
        query += "dos.SeasonStartMonth, ";
        query += "dos.SeasonStartDay, ";
        query += "ds.SeasonEndMonth, ";
        query += "ds.SeasonEndDay ";
        query += "FROM YukonPaObject yp ";
        query += "JOIN CcSeasonStrategyAssignment sa ";
        query += "ON yp.PaObjectId = sa.PaObjectId ";
        query += "JOIN CapControlStrategy strat ";
        query += "ON strat.StrategyId = sa.StrategyId ";
        query += "JOIN DateOfSeason dos ";
        query += "ON sa.SeasonScheduleId = dos.SeasonScheduleId ";
        query += "AND sa.SeasonName = dos.SeasonName ";
        query += "JOIN DateOfSeason ds ";
        query += "ON dos.SeasonScheduleId = ds.SeasonScheduleId ";
        query += "AND dos.SeasonName NOT LIKE '&2&%' ";
        query += "AND ds.SeasonName NOT LIKE '&1&%' ";
        query += "WHERE strat.StrategyId <> 0  ";
        
        //Filter here by Selected Strategy
        if( strategyIds.size() > 0) {
            query += "and (";
            
            for (int i = 0 ; i < strategyIds.size(); i++) {
                query += " strat.strategyid = " + strategyIds.get(i);
                if (i < strategyIds.size()-1) {
                    query += " OR ";
                }
            }
            query += " ) ";
        }
        
        query += "order by strategyname, paoname ";
        
        return query;
    }

    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }

    public String getSelectedStrategy() {
        return selectedStrategy;
    }

    public void setSelectedStrategy(String selectedStrategy) {
        this.selectedStrategy = selectedStrategy;
    }
    
    @Override
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        //No filter used.
    }

    @Override
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        //No filter used.
    }

    @Override
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        //No filter used.
    }

    @Override
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        //No filter used.
    }

    @Override
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        //No filter used.
    }
    
    @Override
    public void setStrategyIdsFilter(Set<Integer> strategyIds) {
        this.strategyIds = new ArrayList<Integer>(strategyIds);
    }
}
