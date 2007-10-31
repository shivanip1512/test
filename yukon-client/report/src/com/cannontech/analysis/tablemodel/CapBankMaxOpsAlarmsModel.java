package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;


public class CapBankMaxOpsAlarmsModel extends BareReportModelBase<CapBankMaxOpsAlarmsModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public CapBankMaxOpsAlarmsModel() {
    }
    
    static public class ModelRow {
        public String capBankName;
        public String feederName;
        public String maxDailyOps;
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
        return "CapBank Max Ops Alarms Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapBankMaxOpsAlarmsModel.ModelRow row = new CapBankMaxOpsAlarmsModel.ModelRow();

                row.capBankName = rs.getString("capBankName");
                row.feederName = rs.getString("feederName");
                row.maxDailyOps = rs.getString("maxDailyOps");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select yp.paoname as cabBankName, yp1.paoname as feederName, c.maxdailyops, c.maxopDisable, ");
        sql.append("substring(dcb.additionalflags, 7, 1) as maxOpHitFlag ");
        sql.append("from yukonpaobject yp, ");
        sql.append("yukonpaobject yp1, ");
        sql.append("dynamiccccapbank dcb, ");
        sql.append("capbank c,  ");
        sql.append("ccfeederbanklist fb, ");
        sql.append("ccfeedersubassignment fs, ");
        sql.append("ccsubstationsubbuslist ss, ");
        sql.append("ccsubareaassignment sa ");
        sql.append("where yp.paobjectid = dcb.capbankid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and ss.substationbusid = fs.substationbusid ");
        sql.append("and fs.feederid = fb.feederid ");
        sql.append("and yp1.paobjectid = fb.feederid ");
        sql.append("and c.deviceid = fb.deviceid ");
        sql.append("and yp.paobjectid = c.deviceid ");
        sql.append("and c.maxdailyops > 0 and (substring(dcb.additionalflags, 7, 1) = 'y' or c.maxopdisable = 'y') ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "c.deviceid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "fb.feederid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "fs.substationbusid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "ss.substationid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "sa.areaid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        sql.append(";");
        return sql;
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
    
}
