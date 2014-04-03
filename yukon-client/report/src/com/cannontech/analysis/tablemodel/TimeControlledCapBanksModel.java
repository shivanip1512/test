package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;


public class TimeControlledCapBanksModel extends BareReportModelBase<TimeControlledCapBanksModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    // inputs
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    public TimeControlledCapBanksModel() {
    }
    
    static public class ModelRow {
        public String area;
        public String substation;
        public String substationbus;
        public String strategyName;
        public String controlMethod;
        public String startTime;
        public Integer percentOfBanksToClose;
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
        return "Time Controlled Cap Banks Report";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int startMonth = rs.getInt("seasonStartMonth");
                int startDay = rs.getInt("seasonStartDay");
                int endMonth = rs.getInt("seasonEndMonth");
                int endDay = rs.getInt("seasonEndDay");
                // using static method because we want server time
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH) +1;
                int month = cal.get(Calendar.MONTH) + 1;
                MonthDay today = new MonthDay(month, day);
                MonthDay start = new MonthDay(startMonth, startDay);
                MonthDay end = new MonthDay(endMonth, endDay);
                
                // as written, assumes start and end are EXCLUSIVE -- that's probably not correct
                if (today.compareTo(start) > 0 && today.compareTo(end) < 0) {
                    TimeControlledCapBanksModel.ModelRow row = new TimeControlledCapBanksModel.ModelRow();
                    row.area = rs.getString("area");
                    row.substation = rs.getString("substation");
                    row.substationbus = rs.getString("substationbus");
                    row.strategyName = rs.getString("strategyname");
                    row.controlMethod = rs.getString("controlmethod");
                    row.startTime = rs.getString("starttime");
                    row.percentOfBanksToClose = rs.getInt("percentOfBanksToClose");

                    data.add(row);
                }
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select ");
        sql.append("ypa.paoname as area ");
        sql.append(", yps.paoname as substation ");
        sql.append(", yp.paoname as substationbus ");
        sql.append(", strat.strategyname ");
        sql.append(", strat.controlmethod ");
        sql.append(", sts.settingname as starttime");
        sql.append(", sts.settingvalue as PercentOfBanksToClose");
        sql.append(", dos.seasonname ");
        sql.append(", dos.seasonstartmonth ");
        sql.append(", dos.seasonstartday ");
        sql.append(", dos.seasonendmonth ");
        sql.append(", dos.seasonendday ");
        sql.append("from capcontrolstrategy strat ");
        sql.append(", ccseasonstrategyassignment strata ");
        sql.append(", dateofseason dos ");
        sql.append(", ccstrategyTargetSettings sts ");
        sql.append(", yukonpaobject ypa ");
        sql.append(", yukonpaobject yps ");
        sql.append(", yukonpaobject yp ");
        sql.append(", ccsubstationsubbuslist ss ");
        sql.append(", ccsubareaassignment sa ");
        sql.append("where strat.controlmethod = 'TIME_OF_DAY' ");
        sql.append("and strat.strategyid = strata.strategyid ");
        sql.append("and dos.seasonscheduleid = strata.seasonscheduleid ");
        sql.append("and dos.seasonname = strata.seasonname ");
        sql.append("and sts.strategyid = strat.strategyid ");
        sql.append("and sts.settingvalue > 0 ");
        sql.append("and sts.settingtype = 'WEEKDAY' ");
        sql.append("and yp.paobjectid = strata.paobjectid ");
        sql.append("and yp.paobjectid = ss.substationbusid ");
        sql.append("and yps.paobjectid = ss.substationid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and ypa.paobjectid = sa.areaid ");
        
        String result = null;
        
        if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "yps.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "ypa.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        return sql;
    }

    @Override
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    @Override
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    @Override
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    @Override
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    @Override
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    @Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
    
    private class MonthDay implements Comparable<MonthDay> {
        private final int month;
        private final int day;

        public MonthDay(int month, int day) {
            this.month = month;
            this.day = day;
        }
        
        @Override
        public int compareTo(MonthDay o) {
            if (this.month == o.month) {
                if (this.day == o.day) {
                    return 0;
                } else if (this.day < o.day) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (this.month < o.month) {
                return -1;
            } else {
                return 1;
            }
        }
    }
    
}
