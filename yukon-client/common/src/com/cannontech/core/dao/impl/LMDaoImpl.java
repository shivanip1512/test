package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Insert the type's description here. Creation date: (3/26/2001 9:40:33 AM)
 * @author:
 */
public final class LMDaoImpl implements LMDao {
    private IDatabaseCache databaseCache;
    private PaoDao paoDao;

    private SimpleJdbcOperations jdbcOps;

    private static final ParameterizedRowMapper<Integer> controlAreaProgramIDRowMapper = new ParameterizedRowMapper<Integer>() {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            int programid = rs.getInt("lmprogramdeviceid");
            return programid;
        }
    };

    /**
     * LMFuncs constructor comment.
     */
    public LMDaoImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.LMDao#getAllLMScenarios()
     */
    public LiteYukonPAObject[] getAllLMScenarios() {
        // Get an instance of the cache.
        ArrayList scenarioList = new ArrayList(32);
        synchronized (databaseCache) {
            List lmScenarios = databaseCache.getAllLMScenarios();
            Collections.sort(lmScenarios, LiteComparators.liteStringComparator);

            for (int i = 0; i < lmScenarios.size(); i++) {
                LiteYukonPAObject litePao = (LiteYukonPAObject) lmScenarios.get(i);
                scenarioList.add(litePao);
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
        scenarioList.toArray(retVal);
        return retVal;
    }
    
    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.LMDao#getAllLMScenarios()
     */
    public LiteYukonPAObject[] getAllLMScenario(Integer scenarioId) {
        // Get an instance of the cache.
        ArrayList scenarioList = new ArrayList(32);
        synchronized (databaseCache) {
            List lmScenarios = databaseCache.getAllLMScenarios();
            Collections.sort(lmScenarios, LiteComparators.liteStringComparator);

            for (int i = 0; i < lmScenarios.size(); i++) {
                LiteYukonPAObject litePao = (LiteYukonPAObject) lmScenarios.get(i);
                scenarioList.add(litePao);
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
        scenarioList.toArray(retVal);
        return retVal;
    }

    public synchronized LiteYukonPAObject[] getAllMemberLMScenarios(
            LiteYukonUser yukUser) {
        // Get an instance of the cache.
        ArrayList scenarioList = new ArrayList(32);
        synchronized (databaseCache) {
            List lmScenarios = databaseCache.getAllLMScenarios();
            Collections.sort(lmScenarios, LiteComparators.liteStringComparator);

            for (int i = 0; i < lmScenarios.size(); i++) {
                LiteYukonPAObject litePao = (LiteYukonPAObject) lmScenarios.get(i);
                if (DaoFactory.getAuthDao()
                              .userHasAccessPAO(yukUser, litePao.getYukonID()))
                    scenarioList.add(litePao);
            }
        }

        LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
        scenarioList.toArray(retVal);
        return retVal;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.LMDao#getLMScenarioProgs(int)
     */
    public LiteLMProgScenario[] getLMScenarioProgs(int scenarioID) {
        // Get an instance of the cache.
        ArrayList progList = new ArrayList(32);
        synchronized (databaseCache) {
            List lmProgs = databaseCache.getAllLMScenarioProgs();
            // Collections.sort( lmProgs, LiteComparators.liteStringComparator
            // );

            for (int i = 0; i < lmProgs.size(); i++) {
                LiteLMProgScenario liteProg = (LiteLMProgScenario) lmProgs.get(i);
                if (scenarioID == liteProg.getScenarioID())
                    progList.add(liteProg);
            }
        }

        LiteLMProgScenario retVal[] = new LiteLMProgScenario[progList.size()];
        progList.toArray(retVal);
        return retVal;
    }

    public List<Integer> getProgramsForControlArea(int areaID) {

        String sql = "select lmprogramdeviceid from lmcontrolareaprogram where deviceid = ?";
        List<Integer> programIds = jdbcOps.query(sql,
                                                 controlAreaProgramIDRowMapper,
                                                 areaID);
        return programIds;
    }

    public Set<LiteYukonPAObject> getAllLMDirectPrograms() {
    	List<LiteYukonPAObject> directPrograms = paoDao.getLiteYukonPAObjectByType(DeviceTypes.LM_DIRECT_PROGRAM);
    	List<LiteYukonPAObject> sepPrograms = paoDao.getLiteYukonPAObjectByType(DeviceTypes.LM_SEP_PROGRAM);
    	List<LiteYukonPAObject> allPrograms = Lists.newArrayList(Iterables.concat(directPrograms, sepPrograms));
    	
        return new HashSet<LiteYukonPAObject>(allPrograms);
    }
    
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId) {
        String sql = "select startgear from lmcontrolscenarioprogram where programid = ? and scenarioid = ?";
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        return jdbcOps.queryForInt(sql, new Integer[] {programId, scenarioId});
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public SimpleJdbcOperations getJdbcOps() {
        return jdbcOps;
    }

    public void setJdbcOps(SimpleJdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
}
