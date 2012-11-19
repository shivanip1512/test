package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapbankDaoImpl implements CapbankDao {    
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private DbChangeManager dbChangeManager;
    
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
			capbankAdditional.setAddress(rs.getString("Description"));
			
			return capbankAdditional;
		}
	};
	
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    @Override
    public List<Integer> getUnassignedCapBankIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT DeviceID");
        sql.append("FROM CapBank");
        sql.append("WHERE DeviceID NOT IN");
        sql.append(   "(SELECT DeviceID");
        sql.append(   " FROM CCFeederBankList)");
        sql.append("ORDER BY DeviceID");
        
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("DEVICEID") );
                return i;
            }
        };
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, mapper);
        return listmap;
    }
    
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(int start, int count) {
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
        
        List<LiteCapControlObject> unassignedBanks = orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedBanks);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
	
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     */
    @Override
    public PaoIdentifier getParentFeederIdentifier(int capBankID) throws EmptyResultDataAccessException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT FeederID");
    	sql.append("FROM CCFeederBankList");
    	sql.append("WHERE DeviceID").eq(capBankID);
    	
        int feederId = yukonJdbcTemplate.queryForInt(sql);
        
        return new PaoIdentifier(feederId, PaoType.CAP_CONTROL_FEEDER);
    }   
    
    @Override
    public boolean isSwitchedBank( Integer paoID ){
        //TODO untested
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT OperationalState");
    	sql.append("FROM CapBank");
    	sql.append("WHERE DeviceID").eq(paoID);
        
        String result = yukonJdbcTemplate.queryForString(sql);
        
        BankOpState state = BankOpState.getStateByName(result);
    
        return state == BankOpState.SWITCHED;
    }
    
    @Override
    public boolean assignCapbank(int capbankId, String feederName) {
        YukonPao pao = paoDao.findYukonPao(feederName, PaoType.CAP_CONTROL_FEEDER);
        return (pao == null) ? false : assignCapbank(pao.getPaoIdentifier().getPaoId(), capbankId);
    };

	@Override
	public boolean assignCapbank(int feederId, int capbankId) {
		SqlStatementBuilder tripSql = new SqlStatementBuilder();
		
		tripSql.append("UPDATE CCFeederBankList");
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
		//Manually building this query because of the inner select. .insertInto() method generates bad grammar sql.
		assignSql.append("INSERT INTO CCFeederBankList (FeederID, DeviceID, ControlOrder, CloseOrder, TripOrder) ");
		assignSql.append(   "SELECT " + feederId + ", " + capbankId + ", MAX(ControlOrder) + 1,MAX(CloseOrder) + 1, 1");
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
		
		if (result) {
		    YukonPao capBank = paoDao.getYukonPao(capbankId);
		    YukonPao feeder = paoDao.getYukonPao(feederId);
		    dbChangeManager.processPaoDbChange(capBank, DbChangeType.UPDATE);
		    dbChangeManager.processPaoDbChange(feeder, DbChangeType.UPDATE);
		}
		
		return result;
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
	public PaoIdentifier findCapBankIdByCBC(int cbcId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("SELECT DeviceID");
		sql.append("FROM CapBank");
		sql.append("WHERE ControlDeviceID").eq(cbcId);
		
        try {
            int capBankId = yukonJdbcTemplate.queryForInt(sql);
            
            return new PaoIdentifier(capBankId, PaoType.CAPBANK);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
	}

	@Override
	public CapbankAdditional getCapbankAdditional(int paoId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DeviceId, MaintenanceAreaID, PoleNumber, DriveDirections, Latitude, Longitude,");
		sql.append(   "CapBankConfig, CommMedium, CommStrength, ExtAntenna, AntennaType, LastMaintVisit,");
		sql.append(   "LastInspVisit, OpCountResetDate, PotentialTransformer, MaintenanceReqPend, OtherComments,");
		sql.append(   "OpTeamComments, CBCBattInstallDate, ypo.Description");
		sql.append("FROM CapBankAdditional cba");
		sql.append("JOIN YukonPaObject ypo ON cba.DeviceId = ypo.PAObjectId");
		sql.append("WHERE DeviceId").eq(paoId);
		
		CapbankAdditional capbankAdditional = yukonJdbcTemplate.queryForObject(sql, capBankAdditionalRowMapper);
		
		return capbankAdditional;
	}
}
