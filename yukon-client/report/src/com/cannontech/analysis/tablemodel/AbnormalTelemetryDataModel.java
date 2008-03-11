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
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public AbnormalTelemetryDataModel() {
    }
    
    static public class ModelRow {
        
        public String device;
        public String type;
        public String point;
        public String units;
        public String quality = "";
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
                row.device = rs.getString("paoName");
                row.type = rs.getString("type");
                row.point = rs.getString("pointName");
                row.units = rs.getString("longName");
                Integer quality = rs.getInt("quality");
                try {
                    row.quality = PointQualities.getQuality(quality);
                } catch (CTIPointQuailtyException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", ds.CurrentVarPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolsubstationbus s        ");
          sql.append("join YukonPAObject yp on s.substationbusid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = s.CurrentVarLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCSubstationBus ds on s.substationbusid = ds.SubstationBusID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and ds.currentvarpointquality <> 5 ");
          sql.append("union  ");
          sql.append("select  ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", ds.CurrentVarPointQuality ");
          sql.append("from ");
          sql.append("capcontrolsubstationbus s        ");
          sql.append("join YukonPAObject yp on s.substationbusid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = s.phaseb ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCSubstationBus ds on s.substationbusid = ds.SubstationBusID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and ds.currentvarpointquality <> 5 ");
          sql.append("union  ");
          sql.append("select  ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", ds.CurrentVarPointQuality ");
          sql.append("from ");
          sql.append("capcontrolsubstationbus s ");    
          sql.append("join YukonPAObject yp on s.substationbusid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = s.phasec ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCSubstationBus ds on s.substationbusid = ds.SubstationBusID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and ds.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select  ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", ds.CurrentVoltPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolsubstationbus s ");
          sql.append("join YukonPAObject yp on s.substationbusid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = s.currentVoltLoadPointID   ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCSubstationBus ds on s.substationbusid = ds.SubstationBusID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and ds.currentvoltpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", ds.CurrentwattPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolsubstationbus s ");
          sql.append("join YukonPAObject yp on s.substationbusid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = s.currentwattLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCSubstationBus ds on s.substationbusid = ds.SubstationBusID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and ds.currentwattpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVarPointQuality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.CurrentVarLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVarPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.phaseb ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVarPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.phasec ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVarPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.currentVoltLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId ");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVarPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.currentWattLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvarpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentVoltPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.currentVoltLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentvoltpointquality <> 5 ");
          sql.append("union ");
          sql.append("select ");
          sql.append("yp.paoname ");
          sql.append(", yp.type ");
          sql.append(", yp.PAOName ");
          sql.append(", yp.PAObjectId");
          sql.append(", p.PointName ");
          sql.append(", um.LongName ");
          sql.append(", df.CurrentWattPointQuality as quality ");
          sql.append("from ");
          sql.append("capcontrolfeeder f ");
          sql.append("join YukonPAObject yp on f.feederid = yp.PAObjectID ");
          sql.append("join point p on p.pointid = f.currentWattLoadPointID ");
          sql.append("join PointUnit pu on p.PointID = pu.PointID ");
          sql.append("join UnitMeasure um on pu.UOMID = um.UOMID ");
          sql.append("join DynamicCCfeeder df on f.feederid = df.feederID ");
          sql.append("where ");
          sql.append("p.pointid > 0 ");
          sql.append("and df.currentwattpointquality <> 5 ");
        
        String result = null;
        
        if(feederIds != null && !feederIds.isEmpty()) {
            result = "PAObjectId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "PAObjectId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "PAObjectId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "PAObjectId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }else if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "PAObjectId in ( ";
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
