package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.spring.YukonSpringHook;

public class AbnormalTelemetryDataModel extends BareReportModelBase<AbnormalTelemetryDataModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private CapControlCache capControlCache = (CapControlCache)YukonSpringHook.getBean("capControlCache");

    private static final String SUBBUS_DB_VALUE = CapControlType.SUBBUS.getDbValue();
    private static final String FEEDER_DB_VALUE = CapControlType.FEEDER.getDbValue();
    
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
    
    @Override
    public String getTitle() {
        return "Abnormal Telemetry Data Report";
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
                
                AbnormalTelemetryDataModel.ModelRow row = new AbnormalTelemetryDataModel.ModelRow();
                row.device = rs.getString("paoName");
                row.type = rs.getString("type");
                row.point = rs.getString("pointName");
                row.units = rs.getString("longName");
                Integer quality = rs.getInt("quality");
                Integer paoId = rs.getInt("paobjectid");
                try {
                    row.quality = PointQuality.getPointQuality(quality).getDescription();
                } catch (IllegalArgumentException e) {
                    CTILogger.error("Invalid Point Quality",e);
                }
                
                if(areaIds != null && !areaIds.isEmpty()) {
                    Integer areaId = -1;
                    if(row.type.equalsIgnoreCase(SUBBUS_DB_VALUE)){
                        SubBus bus = capControlCache.getSubBus(paoId);
                        Integer stationId = bus.getParentID();
                        if(stationId > 0) {
                            SubStation sub = capControlCache.getSubstation(stationId);
                            areaId = sub.getParentID();
                        }
                    }else {
                        Feeder fdr = capControlCache.getFeeder(paoId);
                        Integer parentId = fdr.getParentID();
                        if(parentId > 0) {
                            SubBus bus = capControlCache.getSubBus(parentId);
                            Integer stationId = bus.getParentID();
                            if(stationId > 0) {
                                SubStation sub = capControlCache.getSubstation(stationId);
                                areaId = sub.getParentID();
                            }
                        }
                    }
                            
                    if(areaId > 0 && areaIds.contains(areaId)) {
                        data.add(row);
                    }
                }else if(substationIds != null && !substationIds.isEmpty()) {
                    Integer substationId = -1;
                    if(row.type.equalsIgnoreCase(SUBBUS_DB_VALUE)){
                        SubBus bus = capControlCache.getSubBus(paoId);
                        Integer parentId = bus.getParentID();
                        if(parentId > 0) {
                            SubStation sub = capControlCache.getSubstation(parentId);
                            substationId = sub.getCcId();
                        }
                    }else {
                        Feeder fdr = capControlCache.getFeeder(paoId);
                        Integer parentId = fdr.getParentID();
                        if(parentId > 0) {
                            SubBus bus = capControlCache.getSubBus(parentId);
                            Integer busParent = bus.getParentID();
                            if(busParent > 0) {
                                SubStation sub = capControlCache.getSubstation(busParent);
                                substationId = sub.getCcId();
                            }
                        }
                    }
                            
                    if(substationId > 0 && substationIds.contains(substationId)) {
                        data.add(row);
                    }
                }else if(subbusIds != null && !subbusIds.isEmpty()){
                    Integer busId = -1;
                    if(row.type.equalsIgnoreCase(SUBBUS_DB_VALUE)){
                        busId = paoId;
                    }else {
                        Feeder fdr = capControlCache.getFeeder(paoId);
                        Integer parentId = fdr.getParentID();
                        if(parentId > 0) {
                            busId = parentId;
                        }
                    }
                            
                    if(busId > 0 && subbusIds.contains(busId)) {
                        data.add(row);
                    }
                }else if(feederIds != null && !feederIds.isEmpty()){
                    Integer fdrId = -1;
                    if(row.type.equalsIgnoreCase(FEEDER_DB_VALUE)){
                        fdrId = paoId;
                    }
                    
                    if(fdrId > 0 && feederIds.contains(fdrId)) {
                        data.add(row);
                    }
                }else { // All lists were null or empty so we didn't do any filtering.
                    data.add(row);
                }
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
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }

	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
    
}
