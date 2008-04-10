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
    	public String areaName;
    	public String substationName;
    	public String subBusName;
    	public String feederName;
        public String capBankName;
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
        return "Cap Bank Max Ops Alarms Report";
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
                row.areaName = rs.getString("Area");
                row.substationName = rs.getString("Substation");
                row.subBusName = rs.getString("subBus");
                row.feederName = rs.getString("feederName");
                row.capBankName = rs.getString("capBankName");
                row.maxDailyOps = rs.getString("maxDailyOps");
                String additionalFlags = rs.getString("maxOpHitFlag");
                String maxOpDisableFlag = rs.getString("maxopDisable");
                if(additionalFlags.charAt(6) == 'Y' || maxOpDisableFlag.equalsIgnoreCase("Y")) {
                    data.add(row);
                }
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select yp4.paoname Area,  yp3.paoname Substation, yp2.paoname subBus, ");
        sql.append("yp1.paoname feederName, yp.paoname capBankName, c.maxdailyops, c.maxopDisable maxopDisable, ");
        sql.append("dcb.additionalflags maxOpHitFlag ");
        sql.append("from yukonpaobject yp, ");
        sql.append("yukonpaobject yp1, ");
        sql.append("yukonpaobject yp2, ");
        sql.append("yukonpaobject yp3, ");
        sql.append("yukonpaobject yp4, ");
        sql.append("dynamiccccapbank dcb, ");
        sql.append("capbank c,  ");
        sql.append("ccfeederbanklist fb, ");
        sql.append("ccfeedersubassignment fs, ");
        sql.append("ccsubstationsubbuslist ss, ");
        sql.append("ccsubareaassignment sa ");
        sql.append("where yp.paobjectid = dcb.capbankid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and ss.substationbusid = fs.substationbusid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and yp2.paobjectid = ss.substationbusid ");
        sql.append("and yp3.paobjectid = ss.substationid ");
        sql.append("and yp4.paobjectid = sa.areaid ");
        sql.append("and fs.feederid = fb.feederid ");
        sql.append("and yp1.paobjectid = fb.feederid ");
        sql.append("and c.deviceid = fb.deviceid ");
        sql.append("and yp.paobjectid = c.deviceid ");
        sql.append("and c.maxdailyops > 0 ");
        
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
