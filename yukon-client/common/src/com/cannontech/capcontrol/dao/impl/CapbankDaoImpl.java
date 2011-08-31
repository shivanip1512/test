package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class CapbankDaoImpl implements CapbankDao {
    private static final Logger log = YukonLogManager.getLogger(CapbankDaoImpl.class);
    
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    static {        
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }
    
    private static final YukonRowMapper<CapbankAdditional> capBankAdditionalRowMapper = new YukonRowMapper<CapbankAdditional>() {
		@Override
		public CapbankAdditional mapRow(YukonResultSet rs) throws SQLException {
			PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("DeviceId"), PaoType.CAPBANK);
			CapbankAdditional capbankAdditional = new CapbankAdditional(paoIdentifier);
			
			capbankAdditional.setMaintenanceAreaId(rs.getInt("MaintenanceAreaId"));
			capbankAdditional.setPoleNumber(rs.getInt("PoleNumber"));
			capbankAdditional.setDriveDirections(rs.getString("DriveDirections"));
			capbankAdditional.setLatitude(rs.getDouble("Latitude"));
			capbankAdditional.setLongitude(rs.getDouble("Longitude"));
			capbankAdditional.setCapbankConfig(rs.getString("CapBankConfig"));
			capbankAdditional.setCommMedium(rs.getString("CommMedium"));
			capbankAdditional.setCommStrength(rs.getInt("CommStrength"));
			capbankAdditional.setExtAntenna(rs.getString("ExtAntenna"));
			capbankAdditional.setAntennaType(rs.getString("AntennaType"));
			capbankAdditional.setLastMaintenanceVisit(rs.getDate("LastMaintVisit"));
			capbankAdditional.setLastInspection(rs.getDate("LastInspVisit"));
			capbankAdditional.setOpCountResetDate(rs.getDate("OpCountResetDate"));
			capbankAdditional.setPotentialTransformer(rs.getString("PotentialTransformer"));
			capbankAdditional.setMaintenanceRequired(rs.getString("MaintenanceReqPend"));
			capbankAdditional.setOtherComments(rs.getString("OtherComments"));
			capbankAdditional.setOpTeamComments(rs.getString("OpTeamComments"));
			capbankAdditional.setCbcInstallDate(rs.getDate("CBCBattInstallDate"));
			
			return capbankAdditional;
		}
	};
    
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Override
    public void add(Capbank bank) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("CapBank");
		params.addValue("DeviceId", bank.getPaoId());
		params.addValue("OperationalState", bank.getOperationalState().name());
		params.addValue("ControllerType", bank.getControllerType());
		params.addValue("ControlDeviceId", bank.getControlDeviceId());
		params.addValue("ControlPointId", bank.getControlPointId());
		params.addValue("BankSize", bank.getBankSize());
		params.addValue("TypeOfSwitch", bank.getTypeOfSwitch());
		params.addValue("SwitchManufacture", bank.getSwitchManufacturer());
		params.addValue("MapLocationId", bank.getMapLocationId());
		params.addValue("RecloseDelay", bank.getRecloseDelay());
		params.addValue("MaxDailyOps", bank.getMaxDailyOps());
		params.addValue("MaxOpDisable", bank.getMaxOpDisable());
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
        
		if (result == false) {
			log.error("Update of bank information in CapBank table failed for bank with name: " + bank.getName());
		}
    }
	
    @Override
    public boolean remove(Capbank bank) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("DELETE FROM CapBank");
    	sql.append("WHERE DeviceID").eq(bank.getPaoId());
    	
    	int rowsAffected = yukonJdbcTemplate.update(sql);
    	
		boolean result = (rowsAffected == 1);
		return result;
    }
    
    @Override
    public boolean update(Capbank bank) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	SqlParameterSink params = sql.update("CapBank");
    	params.addValue("OperationalState", bank.getOperationalState());
    	params.addValue("ControllerType", bank.getControllerType());
    	params.addValue("ControlDeviceID", bank.getControlDeviceId());
    	params.addValue("ControlPointID", bank.getControlPointId());
    	params.addValue("BankSize", bank.getBankSize());
    	params.addValue("TypeOfSwitch", bank.getTypeOfSwitch());
    	params.addValue("SwitchManufacture", bank.getSwitchManufacturer());
    	params.addValue("MapLocationID", bank.getMapLocationId());
    	params.addValue("RecloseDelay", bank.getRecloseDelay());
    	params.addValue("MaxDailyOps", bank.getMaxDailyOps());
    	params.addValue("MaxOpDisable", bank.getMaxOpDisable());
    	
    	sql.append("WHERE DeviceID").eq(bank.getPaoId());
    	
    	int rowsAffected = yukonJdbcTemplate.update(sql);
        
    	boolean result = (rowsAffected == 1);
        
		if (result == false) {
			log.debug("Update of bank in CapBank table failed for bank name: " + bank.getName());
		}
        
        return result;
    }
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceID");
        sql.append("FROM CapBank");
        sql.append("WHERE DeviceID NOT IN");
        sql.append(   "(SELECT DeviceID");
        sql.append(   " FROM CCFeederBankList)");
        sql.append("ORDER BY DeviceID");
        
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("DEVICEID") );
                return i;
            }
        };
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, mapper);
        return listmap;
    }
    
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
    	SqlStatementBuilder orphanSql = new SqlStatementBuilder();
    	
    	orphanSql.append("SELECT COUNT(*)");
    	orphanSql.append("FROM CapBank");
    	orphanSql.append("WHERE DeviceID NOT IN");
    	orphanSql.append(   "(SELECT DeviceID");
    	orphanSql.append(   " FROM CCFeederBankList)");
    	
        int orphanCount = yukonJdbcTemplate.queryForInt(orphanSql);
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAPBANK);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT DeviceId");
        sql.append(      " FROM CCFeederBankList)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor<LiteCapControlObject> orphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, liteCapControlObjectRowMapper);
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedBanks = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedBanks);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
	@Override
	public void addCapbankAdditional(CapbankAdditional capbankAdditional) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("CapBankAdditional");
		params.addValue("DeviceID", capbankAdditional.getPaoId());
		params.addValue("MaintenanceAreaID", capbankAdditional.getMaintenanceAreaId());
		params.addValue("PoleNumber", capbankAdditional.getPoleNumber());
		params.addValue("DriveDirections", capbankAdditional.getDriveDirections());
		params.addValue("Latitude", capbankAdditional.getLatitude());
		params.addValue("Longitude", capbankAdditional.getLongitude());
		params.addValue("CapBankConfig", capbankAdditional.getCapbankConfig());
		params.addValue("CommMedium", capbankAdditional.getCommMedium());
		params.addValue("CommStrength", capbankAdditional.getCommStrength());
		params.addValue("ExtAntenna", capbankAdditional.getExtAntenna());
		params.addValue("AntennaType", capbankAdditional.getAntennaType());
		params.addValue("LastMaintVisit", capbankAdditional.getLastMaintenanceVisit());
		params.addValue("LastInspVisit", capbankAdditional.getLastInspection());
		params.addValue("OpCountResetDate", capbankAdditional.getOpCountResetDate());
		params.addValue("PotentialTransformer", capbankAdditional.getPotentialTransformer());
		params.addValue("MaintenanceReqPend", capbankAdditional.getMaintenanceRequired());
		params.addValue("OtherComments", capbankAdditional.getOtherComments());
		params.addValue("OpTeamComments", capbankAdditional.getOpTeamComments());
		params.addValue("CBCBattInstallDate", capbankAdditional.getCbcInstallDate());
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public void updateCapbankAdditional(CapbankAdditional capbankAdditional) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.update("CapBankAdditional");
		params.addValue("MaintenanceAreaID", capbankAdditional.getMaintenanceAreaId());
		params.addValue("PoleNumber", capbankAdditional.getPoleNumber());
		params.addValue("DriveDirections", capbankAdditional.getDriveDirections());
		params.addValue("Latitude", capbankAdditional.getLatitude());
		params.addValue("Longitude", capbankAdditional.getLongitude());
		params.addValue("CapBankConfig", capbankAdditional.getCapbankConfig());
		params.addValue("CommMedium", capbankAdditional.getCommMedium());
		params.addValue("CommStrength", capbankAdditional.getCommStrength());
		params.addValue("ExtAntenna", capbankAdditional.getExtAntenna());
		params.addValue("AntennaType", capbankAdditional.getAntennaType());
		params.addValue("LastMaintVisit", capbankAdditional.getLastMaintenanceVisit());
		params.addValue("LastInspVisit", capbankAdditional.getLastInspection());
		params.addValue("OpCountResetDate", capbankAdditional.getOpCountResetDate());
		params.addValue("PotentialTransformer", capbankAdditional.getPotentialTransformer());
		params.addValue("MaintenanceReqPend", capbankAdditional.getMaintenanceRequired());
		params.addValue("OtherComments", capbankAdditional.getOtherComments());
		params.addValue("OpTeamComments", capbankAdditional.getOpTeamComments());
		params.addValue("CBCBattInstallDate", capbankAdditional.getCbcInstallDate());
		
		sql.append("WHERE DeviceID").eq(capbankAdditional.getPaoId());
		
		yukonJdbcTemplate.update(sql);			
	}
	
	@Override
	public void removeCapbankAdditional(int paoId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CapBankAdditional");
		sql.append("WHERE DeviceID").eq(paoId);
		
		yukonJdbcTemplate.update(sql);    
	}
	
	
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     */
    public int getParentFeederId(int capBankID) throws EmptyResultDataAccessException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT FeederID");
    	sql.append("FROM CCFeederBankList");
    	sql.append("WHERE DeviceID").eq(capBankID);
    	
        return yukonJdbcTemplate.queryForInt(sql);
    }   
    
    public boolean isSwitchedBank( Integer paoID ){
        //TODO untested
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT OperationalState");
    	sql.append("FROM CapBank");
    	sql.append("WHERE DeviceID").eq(paoID);
        
        String result = yukonJdbcTemplate.queryForString(sql);
        
        CapBankOperationalState state = CapBankOperationalState.getStateByName(result);
    
        return state == CapBankOperationalState.SWITCHED;
    }

    public int getParentId(Capbank capbank) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT FeederID");
    	sql.append("FROM CCFeederSubAssignment");
    	sql.append("WHERE DeviceID").eq(capbank.getPaoId());
		
		int id = yukonJdbcTemplate.queryForInt(sql);
		
		return id;
    }
    
	@Override
	public boolean assignCapbank(Feeder feeder, Capbank capbank) {
		return assignCapbank(feeder.getPaoId(),capbank.getPaoId());
	}

	@Override
	public boolean assignCapbank(int feederId, int capbankId) {
		SqlStatementBuilder tripSql = new SqlStatementBuilder();
		
		tripSql.update("CCFeederBankList");
		tripSql.append("SET TripOrder = TripOrder + 1");
		tripSql.append("WHERE FeederId").eq(feederId);
		
		SqlStatementBuilder insertSql = new SqlStatementBuilder();
		
		SqlParameterSink params = insertSql.insertInto("CCFeederBankList");
		params.addValue("FeederID", feederId);
		params.addValue("DeviceID", capbankId);
		params.addValue("ControlOrder", 1);
		params.addValue("CloseOrder", 1);
		params.addValue("TripOrder", 1);
		
		// This guy looks rough.
		SqlStatementBuilder assignSql = new SqlStatementBuilder();
		
		assignSql.insertInto("CCFeederBankList");
		assignSql.append("(FeederID, DeviceID, ControlOrder, CloseOrder, TripOrder)");
		assignSql.append(   "SELECT " + feederId + ", " + capbankId + ", MAX(ControlOrder) + 1,");
		assignSql.append(      "MAX(CloseOrder) + 1, 1");
		assignSql.append(   "FROM CCFeederBankList");
		assignSql.append(   "WHERE FeederID").eq(feederId);
		
		//remove any existing assignment
    	unassignCapbank(capbankId);
    	
    	//Check if any assignments exist.
    	int rowsAffected = 0;
    	
    	SqlStatementBuilder countSql = new SqlStatementBuilder();
    	
    	countSql.append("SELECT COUNT(FeederID)");
    	countSql.append("FROM CCFeederBankList");
    	countSql.append("WHERE FeederID").eq(feederId);
    	
    	int count = yukonJdbcTemplate.queryForInt(countSql);
    	
    	if (count > 0) {
	    	//Change trip orders to +1 so the new one can be 1
			yukonJdbcTemplate.update(tripSql);
			
			//Insert new assignment
			rowsAffected = yukonJdbcTemplate.update(assignSql);
    	} else {
    		//This is the first assignment. Insert with defaults
    		rowsAffected = yukonJdbcTemplate.update(insertSql);
    	}
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignCapbank(Capbank capbank) {
		return unassignCapbank(capbank.getPaoId());
	}

	@Override
	public boolean unassignCapbank(int capbankId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CCFeederBankList");
		sql.append("WHERE DeviceID").eq(capbankId);
    	
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public int getCapBankIdByCBC(int paoId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT DeviceID");
		sql.append("FROM CapBank");
		sql.append("WHERE ControlDeviceID").eq(paoId);
		
        int capBankId = -1;
        try {
            capBankId = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
        return capBankId;
	}

	@Override
	public CapbankAdditional getCapbankAdditional(int paoId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DeviceId, MaintenanceAreaID, PoleNumber, DriveDirections, Latitude, Longitude,");
		sql.append(   "CapBankConfig, CommMedium, CommStrength, ExtAntenna, AntennaType, LastMaintVisit,");
		sql.append(   "LastInspVisit, OpCountResetDate, PotentialTransformer, MaintenanceReqPend, OtherComments,");
		sql.append(   "OpTeamComments, CBCBattInstallDate");
		sql.append("FROM CapBankAdditional");
		sql.append("WHERE DeviceId").eq(paoId);
		
		CapbankAdditional capbankAdditional = yukonJdbcTemplate.queryForObject(sql, capBankAdditionalRowMapper);
		
		return capbankAdditional;
	}
}
