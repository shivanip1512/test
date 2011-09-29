package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.util.Validator;

public class FeederDaoImpl implements FeederDao {
	private static final Logger log = YukonLogManager.getLogger(FeederDaoImpl.class);
    
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private PaoDao paoDao;
    private DBPersistentDao dbPersistentDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }

    private static final ParameterizedRowMapper<Feeder> rowMapper = new ParameterizedRowMapper<Feeder>() {
        public Feeder mapRow(ResultSet rs, int rowNum) throws SQLException {
        	PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("FeederID"), PaoType.CAP_CONTROL_FEEDER);
            Feeder feeder = new Feeder(paoIdentifier);
            feeder.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
            feeder.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
            feeder.setMapLocationId(rs.getString("MapLocationID"));
            feeder.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
            String data = rs.getString("MultiMonitorControl");
            Validator.isNotNull(data);
            feeder.setMultiMonitorControl(data);
            data = rs.getString("usephasedata");
            Validator.isNotNull(data);
            feeder.setUsePhaseData(data);
            feeder.setPhaseb(rs.getInt("phaseb"));
            feeder.setPhasec(rs.getInt("phasec"));
            data = rs.getString("ControlFlag");
            Validator.isNotNull(data);
            feeder.setControlFlag(data);
            return feeder;
        }
    };

    @Override
    public void add(Feeder feeder) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.insertInto("CapControlFeeder");
    	params.addValue("FeederID", feeder.getPaoId());
    	params.addValue("CurrentVarLoadPointID", feeder.getCurrentVarLoadPointId());
    	params.addValue("CurrentWattLoadPointID", feeder.getCurrentWattLoadPointId());
    	params.addValue("MapLocationID", feeder.getMapLocationId());
    	params.addValue("CurrentVoltLoadPointID", feeder.getCurrentVoltLoadPointId());
    	params.addValue("MultiMonitorControl", feeder.getMultiMonitorControl());
    	params.addValue("UsePhaseData", feeder.getUsePhaseData());
    	params.addValue("PhaseB", feeder.getPhaseb());
    	params.addValue("PhaseC", feeder.getPhasec());
    	params.addValue("ControlFlag", feeder.getControlFlag());
    	
		yukonJdbcTemplate.update(sql);
    }
    
    public Feeder getById(int id) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        
    	sql.append("SELECT YP.PAOName, FeederId, CurrentVarLoadPointID, CurrentWattLoadPointID,");
    	sql.append(   "MapLocationID, CurrentVoltLoadPointID, MultiMonitorControl, UsePhaseData,");
    	sql.append(   "PhaseB, PhaseC, ControlFlag");
    	sql.append("FROM CapControlFeeder, YukonPAObject YP");
    	sql.append("WHERE FeederID = YP.PAObjectID AND FeederID").eq(id);
    	
    	Feeder f = yukonJdbcTemplate.queryForObject(sql, rowMapper);
    	
        return f;
    }
    
    @Override
    public boolean remove(Feeder feeder) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM CapControlFeeder");
    	sql.append("WHERE FeederId").eq(feeder.getPaoId());
    	
        int rowsAffected = yukonJdbcTemplate.update(sql);
        
        boolean result = (rowsAffected == 1);
        
        return result;
    }
    
    @Override
    public boolean update(Feeder feeder) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.update("CapControlFeeder");
    	params.addValue("CurrentVarLoadPointID", feeder.getCurrentVarLoadPointId());
    	params.addValue("CurrentWattLoadPointID", feeder.getCurrentWattLoadPointId());
    	params.addValue("MapLocationID", feeder.getMapLocationId());
    	params.addValue("CurrentVoltLoadPointID", feeder.getCurrentVoltLoadPointId());
    	params.addValue("MultiMonitorControl", feeder.getMultiMonitorControl());
    	params.addValue("UsePhaseData", feeder.getUsePhaseData());
    	params.addValue("PhaseB", feeder.getPhaseb());
    	params.addValue("PhaseC", feeder.getPhasec());
    	params.addValue("ControlFlag", feeder.getControlFlag());
    	
    	sql.append("WHERE FeederId").eq(feeder.getPaoId());
    	
        int rowsAffected = yukonJdbcTemplate.update(sql);
        
        boolean result = (rowsAffected == 1);
        
		if (result == false) {
			log.debug("Update of Feeder, " + feeder.getName() + ", in CapControlFeeder table failed.");
		}
		
        return result;
    }

    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds() {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT FeederID");
    	sql.append("FROM CapControlFeeder");
    	sql.append("WHERE FeederID NOT IN");
    	sql.append(   "(SELECT FeederID");
    	sql.append(    "FROM CCFeederSubAssignment)");
    	sql.append("ORDER BY FeederID");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return listmap;
    }

    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
        int orphanCount = yukonJdbcTemplate.queryForInt("SELECT COUNT(*) FROM CapControlFeeder where FeederId not in (SELECT FeederId FROM CCFeederSubAssignment)");
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_FEEDER);
        sql.append("    AND PAObjectID not in (SELECT FeederId FROM CCFeederSubAssignment)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor<LiteCapControlObject> orphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, 
        																													liteCapControlObjectRowMapper);
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedFeeders = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedFeeders);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT SubstationBusID");
    	sql.append("FROM CCFeederSubAssignment");
    	sql.append("WHERE FeederId").eq(feederID);

        return yukonJdbcTemplate.queryForInt(sql);
    }
    
	@Override
	public boolean assignFeeder(int feederId, String subBusName) {
	    YukonPao pao = paoDao.findYukonPao(subBusName, PaoType.CAP_CONTROL_SUBBUS);
        return (pao == null) ? false : assignFeeder(pao.getPaoIdentifier().getPaoId(), feederId);
	};

	@Override
	public boolean assignFeeder(int substationBusId, int feederId) {
		SqlStatementBuilder displaySql = new SqlStatementBuilder();
		
		displaySql.append("SELECT MAX(DisplayOrder)");
		displaySql.append("FROM CCFeederSubAssignment");
		displaySql.append("WHERE SubStationBusID").eq(substationBusId);
		
		int displayOrder = yukonJdbcTemplate.queryForInt(displaySql);
		
		//remove any existing assignment
    	unassignFeeder(feederId);
		
		SqlStatementBuilder assignSql = new SqlStatementBuilder();
		SqlParameterSink params = assignSql.insertInto("CCFeederSubAssignment");
		
		params.addValue("SubStationBusID", substationBusId);
		params.addValue("FeederID", feederId);
		params.addValue("DisplayOrder", ++displayOrder);
    	

		int rowsAffected = yukonJdbcTemplate.update(assignSql);
		
		boolean result = (rowsAffected == 1);
		
		if (result) {
		    sendDbChange(feederId, CapControlType.FEEDER, DbChangeType.UPDATE);
		    sendDbChange(substationBusId, CapControlType.SUBBUS, DbChangeType.UPDATE);
		}
		
		return result;
	}
	
    private void sendDbChange(int id, CapControlType ccType, DbChangeType type) {
        DBChangeMsg msg = new DBChangeMsg(id, DBChangeMsg.CHANGE_PAO_DB, 
                                          PaoCategory.CAPCONTROL.getDbString(),
                                          ccType.getDbValue(), type);
        dbPersistentDao.processDBChange(msg);
    }

	@Override
	public boolean unassignFeeder(Feeder feeder) {
		return unassignFeeder(feeder.getPaoId());
	}

	@Override
	public boolean unassignFeeder(int feederId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CCFeederSubAssignment");
		sql.append("WHERE FeederID").eq(feederId);
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public int getParentId(Feeder feeder) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT SubStationBusID");
		sql.append("FROM CCFeederSubAssignment");
		sql.append("WHERE FeederID").eq(feeder.getPaoId());
		
		int id = yukonJdbcTemplate.queryForInt(sql);
		
		return id;
	}
    
	@Autowired
	public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
	
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}