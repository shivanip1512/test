package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;


public class StrategyAssignmentModel extends BareReportModelBase<StrategyAssignmentModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    // inputs
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    @SuppressWarnings("unused")
    private Set<Integer> substationIds;
    @SuppressWarnings("unused")
    private Set<Integer> areaIds;
    private String[] deviceTypes;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
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
    
    public String getTitle() {
        return "Stategy Assignment Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
            
        String sql = buildSQLStatement();
        
        jdbcOps.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                StrategyAssignmentModel.ModelRow row = new StrategyAssignmentModel.ModelRow();

                row.strategyName = rs.getString("strategyName");
                row.paoName = rs.getString("paoName");
                row.type = rs.getString("type");
                row.controlMethod = rs.getString("controlMethod");
                row.seasonName = rs.getString("seasonName");
                row.seasonStartDate = rs.getString("seasonStartDate");
                row.seasonEndDate = rs.getString("seasonEndDate");
                
                data.add(row);
            }
        });
            
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public String buildSQLStatement() {
        
        String query = "select ";
        query += "yp.paobjectid, ";
        query += "strat.strategyname, ";
        query += "yp.paoname, ";
        query += "yp.type, ";
        query += "strat.controlmethod, ";
        query += "sa.seasonname, ";
        query += "cast(dos.seasonstartmonth as varchar(2)) + '/' ";
        query += "+ cast(dos.seasonstartday as varchar(2)) + '/' ";
        query += "+ cast(datepart(year,getdate()) as varchar(4)) as seasonStartDate, ";
        query += "cast(dos.seasonendmonth as varchar(2)) + '/' ";
        query += "+ cast(dos.seasonendday as varchar(2)) + '/' ";
        query += "+ cast(datepart(year,getdate()) as varchar(4)) as seasonEndDate ";
        query += "from ccseasonstrategyassignment sa, ";
        query += "yukonpaobject yp, ";
        query += "capcontrolstrategy strat, ";
        query += "dateofseason dos ";
        query += "where yp.paobjectid = sa.paobjectid ";
        query += "and strat.strategyid = sa.strategyid ";
        query += "and sa.seasonscheduleid = dos.seasonscheduleid ";
        query += "and sa.seasonname = dos.seasonname ";
        query += "and ";
        query += "(cast(dos.seasonstartmonth as varchar(2)) + '/' ";
        query += "                  + cast(dos.seasonstartday as varchar(2)) + '/' ";
        query += "                  + cast(datepart(year,getdate()) as varchar(4))) <= getdate() ";
        query += "and ";
        query += "(cast(dos.seasonendmonth as varchar(2)) + '/' ";
        query += "                  + cast(dos.seasonendday as varchar(2)) + '/' ";
        query += "                  + cast(datepart(year,getdate()) as varchar(4))) > getdate() ";
        query += "and strat.strategyid <> 0  ";
        query += "order by strategyname, paoname ";
        
        return query;
    }

    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    public void setDeviceTypes(String[] deviceTypes) {
        this.deviceTypes = deviceTypes;
    }
    
}
