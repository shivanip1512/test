package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    private Set<Integer> areaIds;
    private String orderBy = "schedulename";

    public CapControlScheduleDetailModel() {
    }
    
    static public class ModelRow {
        public String scheduleName;
        public String subName;
        public String feederName;
        public String outgoingCommand;
        public String lastRunTime;
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
        return "CapControl Schedule Detail Report";
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
                row.lastRunTime = rs.getString("lastruntime");
                row.interval = rs.getString("interval");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select ps.schedulename as schedulename, yp.paoname as substationbus, yp1.paoname as feeder, psa.command as outgoingcommand, ");
        sql.append("ps.nextruntime, ps.lastruntime as lastruntime, ps.intervalrate as interval " );
        sql.append("from paoscheduleassignment psa  ");
        sql.append("join yukonpaobject yp on psa.paoid = yp.paobjectid  ");
        sql.append("join ccfeedersubassignment cfs on cfs.substationbusid = yp.paobjectid  ");
        sql.append("join yukonpaobject yp1 on yp1.paobjectid = cfs.feederid  ");
        sql.append("join paoschedule ps on ps.scheduleid = psa.scheduleid and ps.disabled = 'N'  ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = cfs.substationbusid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) as ca on ca.paobjectid = saa.areaid ");
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
        }else if (orderBy .equalsIgnoreCase("Interval")) {
            sql.append("order by interval ");
        }
        
        sql.append(";");
        return sql;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setCapBankIdsFilter(java.util.Set)
     */
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setFeederIdsFilter(java.util.Set)
     */
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setSubbusIdsFilter(java.util.Set)
     */
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setAreaIdsFilter(java.util.Set)
     */
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    public void setOrderBy(String order_) {
        this.orderBy = order_;
    }

}
