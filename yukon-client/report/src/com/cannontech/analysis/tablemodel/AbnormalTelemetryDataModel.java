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


public class AbnormalTelemetryDataModel extends BareReportModelBase<AbnormalTelemetryDataModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public AbnormalTelemetryDataModel() {
    }
    
    static public class ModelRow {
        public String substationBus;
        public String subVarPoint;
        public String subVoltPoint;
        public String subWattPoint;
        public String feederName;
        public String varPoint;
        public String voltPoint;
        public String wattPoint;
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
        return "Abnormal Telemetry Data Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                AbnormalTelemetryDataModel.ModelRow row = new AbnormalTelemetryDataModel.ModelRow();

                row.substationBus = rs.getString("substationBus");
                row.subVarPoint = rs.getString("subVarPoint");
                row.subVoltPoint = rs.getString("subVoltPoint");
                row.subWattPoint = rs.getString("subWattPoint");
                row.feederName = rs.getString("feederName");
                row.varPoint = rs.getString("varPoint");
                row.voltPoint = rs.getString("voltPoint");
                row.wattPoint = rs.getString("wattPoint");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select yp.paoname as substationbus, ");
        sql.append("sp.Pointname as SubVarPoint, ");
        sql.append("spv.Pointname as SubVoltPoint, ");
        sql.append("spw.Pointname as SubWattPoint, ");
        sql.append("yp1.paoname as feederName, ");
        sql.append("p.Pointname as VarPoint, ");
        sql.append("pv.Pointname as VoltPoint, ");
        sql.append("pw.Pointname as WattPoint ");
        sql.append("from yukonpaobject yp, yukonpaobject yp1, capcontrolfeeder f, capcontrolsubstationbus s, ");
        sql.append("ccsubstationsubbuslist sbl, ccsubareaassignment sa, ");
        sql.append("ccfeedersubassignment fs, point p, point pv, point pw, point sp, point spv, point spw, ");
        sql.append("dynamicccsubstationbus ds, dynamicccfeeder df ");
        sql.append("where yp.paobjectid = s.substationbusid and yp1.paobjectid = f.feederid ");
        sql.append("and p.pointid = f.currentvarloadpointid and pw.pointid = f.currentwattloadpointid and pv.pointid = f.currentvoltloadpointid and ");
        sql.append("fs.substationbusid = s.substationbusid and fs.feederid = f.feederid ");
        sql.append("and sbl.substationbusid = fs.substationbusid ");
        sql.append("and sa.substationbusid = sbl.substationid ");
        sql.append("and ds.substationbusid = s.substationbusid and df.feederid = f.feederid ");
        sql.append("and sp.pointid = s.currentvarloadpointid and spw.pointid = s.currentwattloadpointid and spv.pointid = s.currentvoltloadpointid ");
        sql.append("and (p.pointid <> 0 or pv.pointid <> 0 or pw.pointid <> 0 or sp.pointid <> 0 or spv.pointid <> 0 or spw.pointid <> 0) ");
        sql.append("and (df.currentvarpointquality <> 5 or df.currentvoltpointquality <> 5 or df.currentwattpointquality <> 5 or ");
        sql.append("ds.currentvarpointquality <> 5 or ds.currentvoltpointquality <> 5 or ds.currentwattpointquality <> 5)  ");
        
        String result = null;
        
        if(feederIds != null && !feederIds.isEmpty()) {
            result = "f.feederid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "s.substationbusid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "sbl.substationid in ( ";
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
