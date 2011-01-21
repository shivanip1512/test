package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class CapControlScheduleDetailModel extends BareDatedReportModelBase<CapControlScheduleDetailModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private String orderBy = "schedulename";
    
    public static final HashMap<Integer, String> TIME_INTERVAL =  new HashMap<Integer, String>();
 
    static {
        TIME_INTERVAL.put(new Integer(1), "1 sec");
        TIME_INTERVAL.put(new Integer(2), "2 sec");
        TIME_INTERVAL.put(new Integer(5), "5 sec");
        TIME_INTERVAL.put(new Integer(10), "10 sec");
        TIME_INTERVAL.put(new Integer(15), "15 sec");
        TIME_INTERVAL.put(new Integer(30), "30 sec");
        TIME_INTERVAL.put(new Integer(60), "1 min");
        TIME_INTERVAL.put(new Integer(120), "2 min");
        TIME_INTERVAL.put(new Integer(180), "3 min");
        TIME_INTERVAL.put(new Integer(240), "4 min");
        TIME_INTERVAL.put(new Integer(300), "5 min");
        TIME_INTERVAL.put(new Integer(420), "7 min");
        TIME_INTERVAL.put(new Integer(600), "10 min");
        TIME_INTERVAL.put(new Integer(720), "12 min");
        TIME_INTERVAL.put(new Integer(900), "15 min");
        TIME_INTERVAL.put(new Integer(1200), "20 min");
        TIME_INTERVAL.put(new Integer(1500), "25 min");
        TIME_INTERVAL.put(new Integer(1800), "30 min");
        TIME_INTERVAL.put(new Integer(3600), "1 hour");
        TIME_INTERVAL.put(new Integer(7200), "2 hours");
        TIME_INTERVAL.put(new Integer(21600), "6 hours");
        TIME_INTERVAL.put(new Integer(43200), "12 hours");
        TIME_INTERVAL.put(new Integer(86400), "1 day");
        TIME_INTERVAL.put(new Integer(172800), "2 days");
        TIME_INTERVAL.put(new Integer(432000), "5 days");
        TIME_INTERVAL.put(new Integer(604800), "7 days");
        TIME_INTERVAL.put(new Integer(1209600), "14 days");
        TIME_INTERVAL.put(new Integer(2592000), "30 days");
    }
    

    public CapControlScheduleDetailModel() {
    }
    
    static public class ModelRow {
        public String scheduleName;
        public String subName;
        public String feederName;
        public String outgoingCommand;
        public Date lastRunTime;
        public Date nextRunTime;
        public String interval;
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
        return "Cap Control Schedule Detail Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(),  new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapControlScheduleDetailModel.ModelRow row = new CapControlScheduleDetailModel.ModelRow();

                row.scheduleName = rs.getString("schedulename");
                row.subName = rs.getString("substationbus");
                row.feederName = rs.getString("feeder");
                row.outgoingCommand = rs.getString("outgoingcommand");
                row.lastRunTime = rs.getTimestamp("lastruntime");
                row.nextRunTime = rs.getTimestamp("nextruntime");
                
                int seconds = rs.getInt("interval");
                String interval = TIME_INTERVAL.get(seconds);
                row.interval = interval;
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select ps.schedulename schedulename, yp.paoname substationbus, yp1.paoname feeder, psa.command outgoingcommand, ");
        sql.append("ps.nextruntime, ps.lastruntime lastruntime, ps.nextruntime, ps.intervalrate interval " );
        sql.append("from paoscheduleassignment psa  ");
        sql.append("join yukonpaobject yp on psa.paoid = yp.paobjectid  ");
        sql.append("join ccfeedersubassignment cfs on cfs.substationbusid = yp.paobjectid  ");
        sql.append("join yukonpaobject yp1 on yp1.paobjectid = cfs.feederid  ");
        sql.append("join ccsubstationsubbuslist ss on ss.substationbusid = yp.paobjectid  ");
        sql.append("join yukonpaobject yp2 on yp2.paobjectid = ss.substationid  ");
        sql.append("join paoschedule ps on ps.scheduleid = psa.scheduleid and ps.disabled = 'N'  ");
        sql.append("left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = cfs.substationbusid ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) ca on ca.paobjectid = saa.areaid ");
        sql.append("left outer join ccfeederbanklist fbl on fbl.feederid = cfs.feederid ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "fbl.deviceid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "ca.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" where ");
            sql.append(result);
        }
        
        if(orderBy.equalsIgnoreCase("Schedule")) {
            sql.append("order by schedulename ");
        }else if (orderBy .equalsIgnoreCase("Sub Bus")) {
            sql.append("order by substationbus ");
        }else if (orderBy .equalsIgnoreCase("Feeder")) {
            sql.append("order by feeder ");
        }else if (orderBy .equalsIgnoreCase("Outgoing Command")) {
            sql.append("order by outgoingcommand ");
        }else if (orderBy .equalsIgnoreCase("Last Run Time")) {
            sql.append("order by lastruntime ");
        }else if (orderBy .equalsIgnoreCase("Next Run Time")) {
            sql.append("order by nextruntime ");
        }else if (orderBy .equalsIgnoreCase("Interval")) {
            sql.append("order by interval ");
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
    
    public void setOrderBy(String order_) {
        this.orderBy = order_;
    }
    
	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}
