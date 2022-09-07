package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class LMDaoImpl implements LMDao {
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public LiteYukonPAObject[] getAllLMScenarios() {
        // Get an instance of the cache.
        List<LiteYukonPAObject> scenarioList = new ArrayList<>(32);
        synchronized (databaseCache) {
            List<LiteYukonPAObject> lmScenarios = databaseCache.getAllLMScenarios();
            Collections.sort(lmScenarios, LiteComparators.liteStringComparator);

            for (int i = 0; i < lmScenarios.size(); i++) {
                LiteYukonPAObject litePao = lmScenarios.get(i);
                scenarioList.add(litePao);
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
        scenarioList.toArray(retVal);
        return retVal;
    }

    @Override
    public LiteLMProgScenario[] getLMScenarioProgs(int scenarioID) {
        // Get an instance of the cache.
        List<LiteLMProgScenario> progList = new ArrayList<>(32);
        synchronized (databaseCache) {
            List<LiteLMProgScenario> lmProgs = databaseCache.getAllLMScenarioProgs();

            for (int i = 0; i < lmProgs.size(); i++) {
                LiteLMProgScenario liteProg = lmProgs.get(i);
                if (scenarioID == liteProg.getScenarioID()) {
                    progList.add(liteProg);
                }
            }
        }

        LiteLMProgScenario retVal[] = new LiteLMProgScenario[progList.size()];
        progList.toArray(retVal);
        return retVal;
    }

    @Override
    public Set<LiteYukonPAObject> getAllProgramsForCommercialCurtailment() {
    	List<LiteYukonPAObject> directPrograms = paoDao.getLiteYukonPAObjectByType(PaoType.LM_DIRECT_PROGRAM);
    	List<LiteYukonPAObject> sepPrograms = paoDao.getLiteYukonPAObjectByType(PaoType.LM_SEP_PROGRAM);

        return Sets.newHashSet(Iterables.concat(directPrograms, sepPrograms));
    }
    
    @Override
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT startgear");
        sql.append("FROM lmcontrolscenarioprogram");
        sql.append("WHERE programid").eq(programId);
        sql.append(  "AND scenarioid").eq(scenarioId);

        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        return new PaoLoader<>() {
            @Override
            public Map<PaoIdentifier, DeviceCollectionReportDevice> getForPaos(Iterable<PaoIdentifier> identifiers) {
                Map<PaoIdentifier, String> namesForLMPAOs = getNamesForLMPAOs(identifiers);
                Map<PaoIdentifier, DeviceCollectionReportDevice> result =
                    Maps.newHashMapWithExpectedSize(namesForLMPAOs.size());

                for (Entry<PaoIdentifier, String> entry : namesForLMPAOs.entrySet()) {
                    DeviceCollectionReportDevice dcrd =
                        new DeviceCollectionReportDevice(entry.getKey().getPaoIdentifier());
                    dcrd.setName(entry.getValue());
                    result.put(entry.getKey(), dcrd);
                }

                return result;
            }
        };
    }

    @Override
    public Map<PaoIdentifier, String> getNamesForLMPAOs(Iterable<PaoIdentifier> identifiers) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select ypo.PAOName, ypo.PAObjectID");
                sql.append("from yukonpaobject ypo");
                sql.append("where ypo.Category = 'LOADMANAGEMENT'");
                sql.append("and ypo.PAObjectID").in(subList);
                return sql;
            }
        };

        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        RowMapper<Entry<Integer, String>> rowMapper =
            new RowMapper<>() {
                @Override
                public Entry<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Maps.immutableEntry(rs.getInt("PAObjectID"), rs.getString("PAOName"));
                }
            };

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }
}
