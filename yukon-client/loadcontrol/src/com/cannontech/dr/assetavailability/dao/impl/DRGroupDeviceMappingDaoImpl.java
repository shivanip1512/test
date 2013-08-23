package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
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
    
    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
    }
    
    @Override
    public Set<Integer> getDeviceIdsForGrouping(PaoIdentifier paoIdentifier) {
        Collection<Integer> loadGroupIds = getLoadGroupIdsForDrGroup(paoIdentifier);
        Set<Integer> deviceIds = Sets.newHashSet(getInventoryAndDeviceIdsForLoadGroups(loadGroupIds).values());
        return deviceIds;
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
        
        List<Pair<Integer, Integer>> idsList = chunkingSqlTemplate.query(sqlFragmentGenerator, loadGroupIds, new YukonRowMapper<Pair<Integer, Integer>>() {
            public Pair<Integer, Integer> mapRow(YukonResultSet rs) throws SQLException {
                int deviceId = rs.getInt("DeviceId");
                int inventoryId = rs.getInt("InventoryId");
                return new Pair<Integer, Integer>(inventoryId, deviceId);
            }
        });
        Map<Integer, Integer> idsMap = Maps.newHashMap();
        for(Pair<Integer, Integer> pair : idsList) {
            idsMap.put(pair.getFirst(), pair.getSecond());
        }
        return idsMap;
    }
    
    @Override
    public Set<Integer> getLoadGroupIdsForDrGroup(PaoIdentifier paoIdentifier) {
        PaoType paoType = paoIdentifier.getPaoType();
        if(paoType.isLoadGroup()) {
            return ImmutableSet.of(paoIdentifier.getPaoId());
        } else if(paoType.isLmProgram()) {
            Set<Integer> programIdSet = Sets.newHashSet(paoIdentifier.getPaoId());
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIdSet);
            return ImmutableSet.copyOf(groupIds);
        } else if(paoType == PaoType.LM_CONTROL_AREA) {
            Set<Integer> programIds = controlAreaDao.getProgramIdsForControlArea(paoIdentifier.getPaoId());
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            return ImmutableSet.copyOf(groupIds);
        } else if(paoType == PaoType.LM_SCENARIO) {
            Set<Integer> programIds = scenarioDao.findScenarioProgramsForScenario(paoIdentifier.getPaoId()).keySet();
            List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            return ImmutableSet.copyOf(groupIds);
        } else {
            throw new IllegalArgumentException("PAO with id " + paoIdentifier.getPaoId() + " is not a DR grouping.");
        }
    }
}
