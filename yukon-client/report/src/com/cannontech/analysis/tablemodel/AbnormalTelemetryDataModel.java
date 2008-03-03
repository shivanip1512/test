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
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.database.data.point.PointQualities;


public class AbnormalTelemetryDataModel extends BareReportModelBase<AbnormalTelemetryDataModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    public final static String SUB_NAME_STRING = "Substation Bus";
    public final static String FEEDER_NAME_STRING = "Feeder";
    public final static int SUB_NAME_COLUMN = 0;
    public final static int FEEDER_NAME_COLUMN = 7;
    public final static int SUB_VAR_COLUMN = 1;
    
    public AbnormalTelemetryDataModel() {
    }
    
    static public class ModelRow {
        public String subVarQuality = "";
        public String subVoltQuality = "";
        public String subWattQuality = "";
        public String fdrVarQuality = "";
        public String fdrVoltQuality = "";
        public String fdrWattQuality = "";
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
                Integer sVarQuality = rs.getInt("sVarQuality");
                Integer sVoltQuality = rs.getInt("sVoltQuality");
                Integer sWattQuality = rs.getInt("sWattQuality");
                Integer fVarQuality = rs.getInt("fVarQuality");
                Integer fVoltQuality = rs.getInt("fVoltQuality");
                Integer fWattQuality = rs.getInt("fWattQuality");
                try {
                    row.subVarQuality = PointQualities.getQuality(sVarQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                try {
                    row.subVoltQuality = PointQualities.getQuality(sVoltQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                try {
                    row.subWattQuality = PointQualities.getQuality(sWattQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                try {
                    row.fdrVarQuality = PointQualities.getQuality(fVarQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                try {
                    row.fdrVoltQuality = PointQualities.getQuality(fVoltQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                try {
                    row.fdrWattQuality = PointQualities.getQuality(fWattQuality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                
                row.substationBus = rs.getString("substationBus");
                String subVarPoint = rs.getString("subVarPoint");
                Integer subVarInt = Integer.parseInt(subVarPoint);
                if (subVarInt <=0) {
                    subVarPoint = "---";
                }
                row.subVarPoint = subVarPoint;
                String subVoltPoint = rs.getString("subVoltPoint");
                Integer subVoltInt = Integer.parseInt(subVoltPoint);
                if (subVoltInt <=0) {
                    subVoltPoint = "---";
                }
                row.subVoltPoint = subVoltPoint;
                String subWattPoint = rs.getString("subWattPoint");
                Integer subWattInt = Integer.parseInt(subWattPoint);
                if (subWattInt <=0) {
                    subWattPoint = "---";
                }
                row.subWattPoint = subWattPoint;
                row.feederName = rs.getString("feederName");
                String varPoint = rs.getString("varPoint");
                Integer varInt = Integer.parseInt(varPoint);
                if (varInt <=0) {
                    varPoint = "---";
                }
                row.varPoint = varPoint;
                String voltPoint = rs.getString("voltPoint");
                Integer voltInt = Integer.parseInt(voltPoint);
                if (voltInt <=0) {
                    voltPoint = "---";
                }
                row.voltPoint = voltPoint;
                String wattPoint = rs.getString("wattPoint");
                Integer wattInt = Integer.parseInt(wattPoint);
                if (wattInt <=0) {
                    wattPoint = "---";
                }
                row.wattPoint = wattPoint;
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select yp.paoname substationbus, ");
        sql.append("sp.Pointname SubVarPoint, ");
        sql.append("ds.currentvarpointquality sVarQuality, ");
        sql.append("spv.Pointname SubVoltPoint, ");
        sql.append("ds.currentvoltpointquality sVoltQuality, ");
        sql.append("spw.Pointname SubWattPoint, ");
        sql.append("ds.currentwattpointquality sWattQuality, ");
        sql.append("yp1.paoname feederName, ");
        sql.append("p.Pointname VarPoint, ");
        sql.append("df.currentvarpointquality fVarQuality, ");
        sql.append("pv.Pointname VoltPoint, ");
        sql.append("df.currentvoltpointquality fVoltQuality, ");
        sql.append("pw.Pointname WattPoint, ");
        sql.append("df.currentwattpointquality fWattQuality ");
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
