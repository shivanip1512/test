package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;


public class StrategyAssignmentModel extends BareReportModelBase<StrategyAssignmentModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    //inputs
    private String selectedStrategy;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();

	private List<Integer> strategyIds;
    
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
        return "Strategy Assignment Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
            
        String sql = buildSQLStatement();
        
        jdbcOps.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                StrategyAssignmentModel.ModelRow row = new StrategyAssignmentModel.ModelRow();
                Date currentTime = new Date();

                row.strategyName = rs.getString("strategyName");
                row.paoName = rs.getString("paoName");
                row.type = rs.getString("type");
                row.controlMethod = rs.getString("controlMethod");
                row.seasonName = rs.getString("seasonName");
                Date startDate = getDateString(rs.getString("seasonstartmonth"), rs.getString("seasonstartday"));
                Date endDate = getDateString(rs.getString("seasonendmonth"), rs.getString("seasonendday"));
                
                if ( startDate.before(currentTime) && endDate.after(currentTime) ) {
                    row.seasonStartDate = startDate.toString();
                    row.seasonEndDate = endDate.toString();
                    data.add(row);
                }
            }

            private Date getDateString(String month, String day) {
                DateTime dt = new DateTime();
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                String resultDate = month + "/" + day + "/" + dt.getYear();
                Date resultDateTime = new Date();
                try {
                    resultDateTime = df.parse(resultDate);
                } catch (ParseException e) {}
                
                return resultDateTime;
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
        query += "dos.seasonstartmonth, ";
        query += "dos.seasonstartday, ";
        query += "dos.seasonendmonth, ";
        query += "dos.seasonendday ";
        query += "from ccseasonstrategyassignment sa, ";
        query += "yukonpaobject yp, ";
        query += "capcontrolstrategy strat, ";
        query += "dateofseason dos ";
        query += "where yp.paobjectid = sa.paobjectid ";
        query += "and strat.strategyid = sa.strategyid ";
        query += "and sa.seasonscheduleid = dos.seasonscheduleid ";
        query += "and sa.seasonname = dos.seasonname ";
        query += "and strat.strategyid <> 0  ";
        
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

    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
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
