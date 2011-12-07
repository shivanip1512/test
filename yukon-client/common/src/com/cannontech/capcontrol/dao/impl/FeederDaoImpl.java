package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.message.dispatch.message.DbChangeType;

public class FeederDaoImpl implements FeederDao {    
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private PaoDao paoDao;
    private PaoCreationHelper paoCreationHelper;
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
            YNBoolean multiMonitorControl = YNBoolean.valueOf(rs.getString("MultiMonitorControl"));
            feeder.setMultiMonitorControl(multiMonitorControl);
            YNBoolean usePhaseData = YNBoolean.valueOf(rs.getString("usephasedata"));
            feeder.setUsePhaseData(usePhaseData);
            feeder.setPhaseb(rs.getInt("phaseb"));
            feeder.setPhasec(rs.getInt("phasec"));
            YNBoolean controlFlag = YNBoolean.valueOf(rs.getString("ControlFlag"));
            feeder.setControlFlag(controlFlag);
            return feeder;
        }
    };
    
    public Feeder findById(int id) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        
    	sql.append("SELECT YP.PAOName, FeederId, CurrentVarLoadPointID, CurrentWattLoadPointID,");
    	sql.append(   "MapLocationID, CurrentVoltLoadPointID, MultiMonitorControl, UsePhaseData,");
    	sql.append(   "PhaseB, PhaseC, ControlFlag");
    	sql.append("FROM CapControlFeeder, YukonPAObject YP");
    	sql.append("WHERE FeederID = YP.PAObjectID AND FeederID").eq(id);
    	
    	Feeder f = yukonJdbcTemplate.queryForObject(sql, rowMapper);
    	
        return f;
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
		    YukonPao feeder = paoDao.getYukonPao(feederId);
		    YukonPao subBus = paoDao.getYukonPao(substationBusId);
		    paoCreationHelper.processDbChange(feeder, DbChangeType.UPDATE);
		    paoCreationHelper.processDbChange(subBus, DbChangeType.UPDATE);
		}
		
		return result;
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
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
	
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
    }
}