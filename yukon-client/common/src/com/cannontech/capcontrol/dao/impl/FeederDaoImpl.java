package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.model.FeederPhaseData;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class FeederDaoImpl implements FeederDao {    
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    @Autowired private PaoDao paoDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }

    @Override
    public List<LiteYukonPAObject> getUnassignedFeeders() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAO.PAObjectID, PAO.Category, PAO.PAOName,");
        sql.append("    PAO.Type, PAO.PAOClass, PAO.Description, PAO.DisableFlag,");
        sql.append("    D.PORTID, DCS.ADDRESS, DR.routeid"); 
        sql.append("FROM YukonPaObject PAO");
        sql.append("    LEFT OUTER JOIN DeviceDirectCommSettings D ON PAO.PAObjectID = D.DeviceID");
        sql.append("    LEFT OUTER JOIN DeviceCarrierSettings DCS ON PAO.PAObjectID = DCS.DeviceID"); 
        sql.append("    LEFT OUTER JOIN DeviceRoutes DR ON PAO.PAObjectID = DR.DeviceID");
        sql.append("    JOIN CapControlFeeder CCF ON PAO.PAObjectID = CCF.FeederID");
        sql.append("WHERE PAO.PAObjectID NOT IN");
        sql.append(   "(SELECT FeederId");
        sql.append(   " FROM CCFeederSubAssignment)");
        
        List<LiteYukonPAObject> feeders = yukonJdbcTemplate.query(sql, new LitePaoRowMapper());
        
        return feeders;
    }
    
    /**
     * This method returns all the Feeder IDs that are assigned
     *  to the SubBus passed in.
     */
    @Override
    public List<Integer> getFeederIdBySubstationBus(YukonPao subbus) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT FeederID");
        sql.append("FROM CapControlFeeder");
        sql.append("WHERE FeederID  IN");
        sql.append(   "(SELECT FeederID");
        sql.append(    "FROM CCFeederSubAssignment WHERE SubstationBusId = " + subbus.getPaoIdentifier().getPaoId() +")");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return listmap;
    }
    
    @Override
    public FeederPhaseData getFeederPhaseData(int feederId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CurrentVarLoadPointId, PhaseB, PhaseC, UsePhaseData");
        sql.append("FROM CapControlFeeder");
        sql.append("WHERE FeederId").eq(feederId);
        
        ParameterizedRowMapper<FeederPhaseData> mapper = 
                new ParameterizedRowMapper<FeederPhaseData>() {
            
            @Override
            public FeederPhaseData mapRow(ResultSet rs, int rowNum) throws SQLException {
                int currentVarLoadPointId = rs.getInt("CurrentVarLoadPointId");
                int phaseB = rs.getInt("PhaseB");
                int phaseC = rs.getInt("PhaseC");
                boolean usePhaseData = ("Y").equals(rs.getString("UsePhaseData"));
                
                return new FeederPhaseData(currentVarLoadPointId, phaseB, phaseC, usePhaseData);
            }
        };
        
        return yukonJdbcTemplate.queryForObject(sql, mapper);
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
        
        List<LiteCapControlObject> unassignedFeeders = orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedFeeders);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     */
    @Override
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
		    dbChangeManager.processPaoDbChange(feeder, DbChangeType.UPDATE);
		    dbChangeManager.processPaoDbChange(subBus, DbChangeType.UPDATE);
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

    @Override
    public PointIdContainer getFeederPointIds(int feederId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
                
        sql.append("SELECT CurrentVarLoadPointId, CurrentWattLoadPointId, CurrentVoltLoadPointId, UsePhaseData, PhaseB, PhaseC");
        sql.append("FROM CapControlFeeder");
        sql.append("WHERE FeederId").eq(feederId);
            
        PointIdContainer points = yukonJdbcTemplate.queryForObject(sql, pointIdContainerMapper);
            
        return points;
    }
}