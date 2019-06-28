package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DRGroupDeviceMappingDaoImpl implements DRGroupDeviceMappingDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private ChunkingSqlTemplate chunkingSqlTemplate;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ProgramDao programDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private RfnGatewayService rfnGatewayService;

    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
    }

    @Override
    public Map<Integer, SimpleDevice> getInventoryPaoMapForGrouping(YukonPao yukonPao) {
        Collection<Integer> loadGroupIds = getLoadGroupIdsForDrGroup(yukonPao.getPaoIdentifier());

        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PaObjectId, ypo.Type, ib.InventoryId");
                sql.append("FROM YukonPAObject ypo");
                sql.append("JOIN InventoryBase ib ON ib.DeviceId = ypo.PaObjectId");
                sql.append("JOIN LMHardwareConfiguration lmhc ON lmhc.InventoryId = ib.InventoryId");
                sql.append("WHERE lmhc.AddressingGroupId").in(subList);
                sql.append("AND PaObjectId").gt(0);
                return sql;
            }
        };

        final Map<Integer, SimpleDevice> results = new HashMap<>();
        chunkingSqlTemplate.query(sqlFragmentGenerator, loadGroupIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int inventoryId = rs.getInt("InventoryId");
                SimpleDevice device = new SimpleDevice(rs.getPaoIdentifier("PaObjectId", "Type"));
                results.put(inventoryId, device);
            }
        });
        return results;
    }

    @Override
    public Map<Integer, Integer> getInventoryAndDeviceIdsForLoadGroups(Iterable<Integer> loadGroupIds) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.DeviceId, ib.InventoryId");
                sql.append("FROM LMHardwareConfiguration lmhc");
                sql.append("JOIN InventoryBase ib ON ib.InventoryId = lmhc.InventoryId");
                sql.append("WHERE lmhc.AddressingGroupId").in(subList);
                return sql;
            }
        };

        final Map<Integer, Integer> idsMap = Maps.newHashMap();
        chunkingSqlTemplate.query(sqlFragmentGenerator, loadGroupIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                idsMap.put(rs.getInt("InventoryId"), rs.getInt("DeviceId"));
            }
        });

        return idsMap;
    }

    @Override
    public List<String> getSerialNumbersForLoadGroups(Iterable<Integer> loadGroupIds) {
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ManufacturerSerialNumber");
                sql.append("FROM LMHardwareConfiguration lmhc");
                sql.append("JOIN InventoryBase ib ON ib.InventoryId = lmhc.InventoryId");
                sql.append("JOIN LmHardwareBase lhb on lhb.InventoryID = ib.InventoryID");
                sql.append("WHERE lmhc.AddressingGroupId").in(subList);
                return sql;
            }
        };

        List<String> serialNumbers = chunkingSqlTemplate.query(sqlFragmentGenerator, loadGroupIds, TypeRowMapper.STRING);

        return serialNumbers;
    }

    @Override
    public Set<Integer> getLoadGroupIdsForDrGroup(PaoIdentifier paoIdentifier) {
        PaoType paoType = paoIdentifier.getPaoType();
        if (paoType.isLoadGroup()) {
            return ImmutableSet.of(paoIdentifier.getPaoId());
        } else if (paoType.isLmProgram()) {
            Set<Integer> programIdSet = Sets.newHashSet(paoIdentifier.getPaoId());
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIdSet);
            return ImmutableSet.copyOf(groupIds);
        } else if (paoType == PaoType.LM_CONTROL_AREA) {
            Set<Integer> programIds = controlAreaDao.getProgramIdsForControlArea(paoIdentifier.getPaoId());
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            return ImmutableSet.copyOf(groupIds);
        } else if (paoType == PaoType.LM_SCENARIO) {
            Set<Integer> programIds = scenarioDao.findScenarioProgramsForScenario(paoIdentifier.getPaoId()).keySet();
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            return ImmutableSet.copyOf(groupIds);
        } else {
            throw new IllegalArgumentException("PAO with id " + paoIdentifier.getPaoId() + " is not a DR grouping.");
        }
    }
    
    @Override
    public List<RfnGateway> getRfnGatewayList(Iterable<Integer> loadGroupIds) {
        SqlStatementBuilder allGatewaysSql = buildGatewaySelect(loadGroupIds);
        return jdbcTemplate.query(allGatewaysSql, TypeRowMapper.INTEGER)
                .stream()
                .map(rfnGatewayService::getGatewayByPaoId)
                .collect(Collectors.toList());
    }

    private SqlStatementBuilder buildGatewaySelect(Iterable<Integer> loadGroupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT drdd.GatewayId ");
        sql.append("FROM LMHardwareBase lmbase, LMHardwareConfiguration hdconf, InventoryBase inv ");
        sql.append("JOIN YukonPAObject ypo ON (inv.deviceID = ypo.PAObjectID) ");
        sql.append("LEFT OUTER JOIN DynamicRfnDeviceData drdd ON (inv.DeviceID = drdd.DeviceId) ");
        sql.append("WHERE inv.InventoryID = lmbase.InventoryID ");
        sql.append("AND lmbase.InventoryID = hdconf.InventoryID AND lmbase.InventoryID IN ");
        sql.append("(SELECT DISTINCT InventoryId ");
        sql.append("FROM LMHardwareConfiguration) ");
        sql.append("AND drdd.GatewayID IS NOT NULL ");
        sql.append("AND AddressingGroupID").in(loadGroupIds);
        sql.append("ORDER BY drdd.GatewayId ASC");
        return sql;
    }
}
