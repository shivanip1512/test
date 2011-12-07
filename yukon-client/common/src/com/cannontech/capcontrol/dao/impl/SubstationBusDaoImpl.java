package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.SubstationBus;
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
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.util.Validator;

public class SubstationBusDaoImpl implements SubstationBusDao {
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private PaoCreationHelper paoCreationHelper;
    private PaoDao paoDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }
    
    private static final ParameterizedRowMapper<SubstationBus> rowMapper = new ParameterizedRowMapper<SubstationBus>() {
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

    public SubstationBus findSubBusById(int id){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT YP.PAOName, SubstationBusID, CurrentVarLoadPointID, CurrentWattLoadPointID,");
        sql.append(   "MapLocationID, CurrentVoltLoadPointID, AltSubID, SwitchPointID, DualBusEnabled,");
        sql.append(   "MultiMonitorControl, UsePhasedata, PhaseB, PhaseC, ControlFlag, VoltReductionPointId,");
        sql.append(   "DisableBusPointId");
        sql.append("FROM CapControlSubstationBus, YukonPAObject YP");
        sql.append("WHERE SubstationBusID = YP.PAObjectID AND SubstationBusID").eq(id);
        
        SubstationBus sub = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        
        return sub;
    }
    
    public List<Integer> getAllUnassignedBuses () {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT SubstationBusID");
        sql.append("FROM CapControlSubstationBus");
        sql.append("WHERE SubstationBusID NOT IN");
        sql.append(   "(SELECT SubstationBusID");
        sql.append(   " FROM CCSubstationSubBusList)");
        sql.append("ORDER BY SubstationBusID");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return listmap;
    }

    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
    	SqlStatementBuilder orphanSql = new SqlStatementBuilder();
    	
    	orphanSql.append("SELECT COUNT(*)");
    	orphanSql.append("FROM CapControlSubstationBus");
    	orphanSql.append("WHERE SubstationBusID NOT IN");
    	orphanSql.append(   "(SELECT SubstationBusID");
    	orphanSql.append(   " FROM CCSubstationSubBusList)");
    	
        int orphanCount = yukonJdbcTemplate.queryForInt(orphanSql);
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_SUBBUS);
        sql.append("    AND PAObjectID not in (SELECT SubstationBusId FROM CCSubstationSubBusList)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor<LiteCapControlObject> orphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, liteCapControlObjectRowMapper);
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedBuses = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedBuses);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
	
    @Override
    public boolean assignSubstationBus(int subBusId, String substationName) {
    	YukonPao pao = paoDao.findYukonPao(substationName, PaoType.CAP_CONTROL_SUBSTATION);
    	return (pao == null) ? false : assignSubstationBus(pao.getPaoIdentifier().getPaoId(), subBusId);
    }
    
    @Override
    public boolean assignSubstationBus(int substationId, int substationBusId) {
    	SqlStatementBuilder displaySql = new SqlStatementBuilder();
    	
    	displaySql.append("SELECT MAX(DisplayOrder)");
    	displaySql.append("FROM CCSubstationSubBusList");
    	displaySql.append("WHERE SubstationID").eq(substationId);
    	
		int displayOrder = yukonJdbcTemplate.queryForInt(displaySql);
		
		//remove any existing assignment
    	unassignSubstationBus(substationBusId);
    	
    	SqlStatementBuilder assignSql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = assignSql.insertInto("CCSubstationSubBusList");
    	params.addValue("SubstationID", substationId);
    	params.addValue("SubstationBusID", substationBusId);
    	params.addValue("DisplayOrder", ++displayOrder);

		int rowsAffected = yukonJdbcTemplate.update(assignSql);
		
		boolean result = (rowsAffected == 1);
		
		if (result) {
		    YukonPao subBus = paoDao.getYukonPao(substationBusId);
		    YukonPao substation = paoDao.getYukonPao(substationId);
		    paoCreationHelper.processDbChange(subBus, DbChangeType.UPDATE);
		    paoCreationHelper.processDbChange(substation, DbChangeType.UPDATE);
		}
		
		return result;
    }
    
    @Override
    public boolean unassignSubstationBus(int substationBusId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM CCSubstationSubBusList");
    	sql.append("WHERE SubstationBusID").eq(substationBusId);
    	
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		return result;
    }
	
	@Override
    public Collection<Integer> getBankStatusPointIdsBySubbusId(int substationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT P.PointId");
        sql.append("FROM Point P");
        sql.append("WHERE P.POINTTYPE = 'Status'");
        sql.append("  AND P.PointOffset = 1 ");
        sql.append("  AND P.PAObjectID IN (SELECT DeviceId ");
        sql.append("                       FROM CCFeederBankList");
        sql.append("                       WHERE FeederId IN (SELECT FeederId ");
        sql.append("                                          FROM CCFeederSubAssignment ");
        sql.append("                                          WHERE SubStationBusID").eq(substationId).append("))");
        
        
        List<Integer> statusPointIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return statusPointIds;
    }
	
	@Autowired
	public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
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