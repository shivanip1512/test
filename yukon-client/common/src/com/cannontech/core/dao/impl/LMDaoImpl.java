package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;
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
}
