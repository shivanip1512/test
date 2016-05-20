package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.CymePaoPoint;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.util.Validator;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class SubstationBusDaoImpl implements SubstationBusDao {
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ZoneDao zoneDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    
    private static final RowMapper<SubstationBus> rowMapper = new RowMapper<SubstationBus>() {
        @Override
        public SubstationBus mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("SubstationBusID"), PaoType.CAP_CONTROL_SUBBUS);
            SubstationBus bus = new SubstationBus(paoIdentifier);
            bus.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
            bus.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
            bus.setMapLocationId(rs.getString("MapLocationID"));
            bus.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
            bus.setAltSubId(rs.getInt("AltSubID"));
            bus.setSwitchPointId(rs.getInt("SwitchPointID"));
            String data = rs.getString("DualBusEnabled");
            Validator.isNotNull(data);
            bus.setDualBusEnabled(data);
            data = rs.getString("MultiMonitorControl");
            Validator.isNotNull(data);
            bus.setMultiMonitorControl(data);
            data = rs.getString("usephasedata");
            Validator.isNotNull(data);
            bus.setUsephasedata(data);
            bus.setPhaseb(rs.getInt("phaseb"));
            bus.setPhasec(rs.getInt("phasec"));
            data = rs.getString("DualBusEnabled");
            Validator.isNotNull(data);
            bus.setControlFlag(data);
            bus.setVoltReductionPointId(rs.getInt("VoltReductionPointId"));
            bus.setDisabledPointId(rs.getInt("DisableBusPointId"));
            
            return bus;
        }
    };
    
    @Override
    public SubstationBus findSubBusById(int id){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT YP.PAOName, SubstationBusID, CurrentVarLoadPointID, CurrentWattLoadPointID,");
        sql.append(   "MapLocationID, CurrentVoltLoadPointID, AltSubID, SwitchPointID, DualBusEnabled,");
        sql.append(   "MultiMonitorControl, UsePhasedata, PhaseB, PhaseC, ControlFlag, VoltReductionPointId,");
        sql.append(   "DisableBusPointId");
        sql.append("FROM CapControlSubstationBus, YukonPAObject YP");
        sql.append("WHERE SubstationBusID = YP.PAObjectID AND SubstationBusID").eq(id);
        
        SubstationBus sub = jdbcTemplate.queryForObject(sql, rowMapper);
        
        return sub;
    }
    
    @Override
    public List<PaoIdentifier> getAllSubstationBuses() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAO.PaObjectID, PAO.Type");
        sql.append("FROM YukonPaObject PAO");
        sql.append("    JOIN CapControlSubstationBus S ON PAO.PaObjectID = S.SubstationBusID");
        
        List<PaoIdentifier> listmap = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);
        
        return listmap;
    }
    
    @Override
    public List<LiteYukonPAObject> getUnassignedBuses() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAO.PAObjectID, PAO.Category, PAO.PAOName,");
        sql.append("    PAO.Type, PAO.PAOClass, PAO.Description, PAO.DisableFlag,");
        sql.append("    D.PORTID, DCS.ADDRESS, DR.routeid"); 
        sql.append("FROM YukonPaObject PAO");
        sql.append("    LEFT OUTER JOIN DeviceDirectCommSettings D ON PAO.PAObjectID = D.DeviceID");
        sql.append("    LEFT OUTER JOIN DeviceCarrierSettings DCS ON PAO.PAObjectID = DCS.DeviceID"); 
        sql.append("    LEFT OUTER JOIN DeviceRoutes DR ON PAO.PAObjectID = DR.DeviceID");
        sql.append("    JOIN CapControlSubStationBus SB ON PAO.PAObjectID = SB.SubstationBusID");
        sql.append("WHERE PAO.PAObjectID NOT IN");
        sql.append(   "(SELECT SubstationBusID");
        sql.append(   " FROM CCSubstationSubBusList)");
        
        List<LiteYukonPAObject> buses = jdbcTemplate.query(sql, new LitePaoRowMapper());
        
        return buses;
    }
    
    @Override
    public List<LiteCapControlObject> getOrphans() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_SUBBUS);
        sql.append("    AND PAObjectID not in (SELECT SubstationBusId FROM CCSubstationSubBusList)");
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = jdbcTemplate.query(sql, CapbankControllerDao.LITE_ORPHAN_MAPPER);
        
        return orphans;
    }
    
    @Override
    public boolean assignSubstationBus(YukonPao substation, YukonPao substationBus) {
        SqlStatementBuilder displaySql = new SqlStatementBuilder();
        int substationId = substation.getPaoIdentifier().getPaoId();
        
        displaySql.append("SELECT MAX(DisplayOrder)");
        displaySql.append("FROM CCSubstationSubBusList");
        displaySql.append("WHERE SubstationID").eq(substationId);

        int displayOrder = jdbcTemplate.queryForInt(displaySql);

        // remove any existing assignment
        unassignSubstationBus(substationBus);

        SqlStatementBuilder assignSql = new SqlStatementBuilder();

        SqlParameterSink params = assignSql.insertInto("CCSubstationSubBusList");
        params.addValue("SubstationID", substationId);
        params.addValue("SubstationBusID", substationBus.getPaoIdentifier().getPaoId());
        params.addValue("DisplayOrder", ++displayOrder);

        int rowsAffected = jdbcTemplate.update(assignSql);

        boolean result = (rowsAffected == 1);

        if (result) {
            dbChangeManager.processPaoDbChange(substationBus, DbChangeType.UPDATE);
            dbChangeManager.processPaoDbChange(substation, DbChangeType.UPDATE);
        }

        return result;
    }
    
    @Override
    public boolean unassignSubstationBus(YukonPao substationBus) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("DELETE FROM CCSubstationSubBusList");
        sql.append("WHERE SubstationBusID").eq(substationBus.getPaoIdentifier().getPaoId());

        int rowsAffected = jdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        return result;
    }
    
    @Override
    public List<CymePaoPoint> getBankStatusPointPaoIdsBySubbusId(int substationBusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT P.PointId, YPO.PaoName, YPO.PAObjectId, YPO.Type");
        buildBankStatusPointQueryEnd(sql,substationBusId);        
        
        List<CymePaoPoint> statusPoints = jdbcTemplate.query(sql,cymePaoPointRowMapper);
        
        return statusPoints;
    }

    private void buildBankStatusPointQueryEnd(SqlStatementBuilder sql, int substationBusId) {
        sql.append("FROM Point P");
        sql.append("JOIN YukonPAObject YPO on YPO.PAObjectId = P.PAObjectId");
        sql.append("JOIN CCFeederBankList FBL on P.PAObjectId = FBL.DeviceId");
        sql.append("JOIN CCFeederSubAssignment FSA on FBL.FeederId = FSA.FeederId");
        sql.append("WHERE FSA.SubStationBusId").eq(substationBusId).append("AND P.POINTTYPE = 'Status'");
    }
    
    @Override
    public PointIdContainer getSubstationBusPointIds(int substationBusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT CurrentVarLoadPointId, CurrentWattLoadPointId, CurrentVoltLoadPointId, UsePhaseData, PhaseB, PhaseC");
        sql.append("FROM CapControlSubstationBus");
        sql.append("WHERE SubstationBusId").eq(substationBusId);
        
        try {
            PointIdContainer points = jdbcTemplate.queryForObject(sql, FeederDao.pointIdContainerMapper);
            return points;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Bus not found with Id:" + substationBusId);
        }        
    }

    @Override
    public List<String> getAssignedFeederPaoNames(int subbusId) { 
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YPO.PaoName");
        sql.append("FROM CCFeederSubAssignment FSA");
        sql.append("JOIN YukonPaObject YPO on YPO.PaObjectId = FSA.FeederID");
        sql.append("WHERE FSA.SubStationBusID").eq(subbusId);
        
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }
    
    @Override
    public List<Integer> getRegulatorsForBus(int id) {
        
        List<Integer> regulatorIds = new ArrayList<>();
        
        List<Zone> busZones = zoneDao.getZonesBySubBusId(id);
        
        for (Zone zone : busZones) {
            List<RegulatorToZoneMapping> mappings = zone.getRegulators();
            
            for (RegulatorToZoneMapping mapping : mappings) {
                regulatorIds.add(mapping.getRegulatorId());
            }
            
        }
        
        return regulatorIds;
    }

    @Override
    public Integer getParent(int busId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubStationID FROM CCSubstationSubBusList");
        sql.append("WHERE SubstationBusID").eq(busId);

        Integer parentId;
        try {
            parentId = jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            parentId = null;
        }
        return parentId;
    }

    @Override
    @Transactional
    public void assignFeeders(int busId, Iterable<Integer> feederIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCFeederSubAssignment");
        sql.append("WHERE SubStationBusID").eq(busId);

        jdbcTemplate.update(sql);
        
        sql = new SqlStatementBuilder();
        ArrayList<Integer> feederList = Lists.newArrayList(feederIds);
        sql.append(insertFeeders(busId, feederList));
        jdbcTemplate.update(sql);
    }
    
    private SqlFragmentSource insertFeeders(int busId, ArrayList<Integer> feederList) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleSql =
            builder.buildFor(DatabaseVendor.getOracleDatabases());
        
        SqlBuilder otherSql = builder.buildOther();
        int displayOrder = 1;
          
        oracleSql.append("INSERT ALL");
        for (Integer feederId : feederList) {
            otherSql.append("INSERT INTO CCFeederSubAssignment");
            otherSql.append(" values (").appendArgument(busId).append(", ").appendArgument(feederId).append(", ").appendArgument(displayOrder).append(")");
            oracleSql.append("INTO CCFeederSubAssignment");
            oracleSql.append(" values (").appendArgument(busId).append(", ").appendArgument(feederId).append(", ").appendArgument(displayOrder).append(")");
            displayOrder++;
        }
        
        oracleSql.append("SELECT 1 from dual");

        return builder;
    }
    
    @Override
    @Transactional
    public void assignBuses(int substationId, Iterable<Integer> busIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCSUBSTATIONSUBBUSLIST");
        sql.append("WHERE SubStationID").eq(substationId);

        jdbcTemplate.update(sql);
        
        sql = new SqlStatementBuilder();
        ArrayList<Integer> busList = Lists.newArrayList(busIds);
        sql.append(insertBuses(substationId, busList));
        jdbcTemplate.update(sql);
    }
    
    private SqlFragmentSource insertBuses(int substationId, ArrayList<Integer> busList) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleSql =
            builder.buildFor(DatabaseVendor.getOracleDatabases());
        
        SqlBuilder otherSql = builder.buildOther();
        int displayOrder = 1;
          
        oracleSql.append("INSERT ALL");
        for (Integer busId : busList) {
            otherSql.append("INSERT INTO CCSUBSTATIONSUBBUSLIST");
            otherSql.append(" values (").appendArgument(substationId).append(", ").appendArgument(busId).append(", ").appendArgument(displayOrder).append(")");
            oracleSql.append("INTO CCSUBSTATIONSUBBUSLIST");
            oracleSql.append(" values (").appendArgument(substationId).append(", ").appendArgument(busId).append(", ").appendArgument(displayOrder).append(")");
            displayOrder++;
        }
        
        oracleSql.append("SELECT 1 from dual");

        return builder;
    }
    
    @Override
    public boolean assignBus(int substationId, int busId) {
        YukonPao substation = dbCache.getAllPaosMap().get(substationId);
        YukonPao bus = dbCache.getAllPaosMap().get(busId);
        return assignSubstationBus(substation, bus);
    }

    @Override
    public List<Integer> getFeederIds(int busId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FeederID FROM CCFeederSubAssignment");
        sql.append("WHERE SubStationBusID").eq(busId);
        sql.append("ORDER BY DisplayOrder");

        List<Integer> feederIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);

        return feederIds;
    }
}