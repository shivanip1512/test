package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapbankDaoImpl implements CapbankDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private DbChangeManager dbChangeManager;
    
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
    
    @Override
    public List<LiteYukonPAObject> getUnassignedCapBanks() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAO.PAObjectID, PAO.Category, PAO.PAOName,");
        sql.append("    PAO.Type, PAO.PAOClass, PAO.Description, PAO.DisableFlag,");
        sql.append("    D.PORTID, DCS.ADDRESS, DR.routeid"); 
        sql.append("FROM YukonPaObject PAO");
        sql.append("    LEFT OUTER JOIN DeviceDirectCommSettings D ON PAO.PAObjectID = D.DeviceID");
        sql.append("    LEFT OUTER JOIN DeviceCarrierSettings DCS ON PAO.PAObjectID = DCS.DeviceID"); 
        sql.append("    LEFT OUTER JOIN DeviceRoutes DR ON PAO.PAObjectID = DR.DeviceID");
        sql.append("    JOIN CAPBANK CB ON PAO.PAObjectID = CB.DEVICEID");
        sql.append("WHERE PAO.PAObjectID NOT IN");
        sql.append(   "(SELECT DEVICEID");
        sql.append(   " FROM CCFeederBankList)");
        
        List<LiteYukonPAObject> banks = yukonJdbcTemplate.query(sql, new LitePaoRowMapper());
        
        return banks;
    }
    
    @Override
    public List<LiteCapControlObject> getOrphans() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAPBANK);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT DeviceId");
        sql.append(      " FROM CCFeederBankList)");
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = yukonJdbcTemplate.query(sql, CapbankControllerDao.LITE_ORPHAN_MAPPER);
        
        return orphans;
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
    public boolean assignCapbank(YukonPao capbank, String feederName) {
        YukonPao feeder = paoDao.findYukonPao(feederName, PaoType.CAP_CONTROL_FEEDER);
        return (feeder == null) ? false : assignCapbank(feeder, capbank);
    }

    @Override
    public boolean assignCapbank(YukonPao feeder, YukonPao capbank) {
        SqlStatementBuilder tripSql = new SqlStatementBuilder();
        int feederId = feeder.getPaoIdentifier().getPaoId();
        int capbankId = capbank.getPaoIdentifier().getPaoId();

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
        // Manually building this query because of the inner select.
        // .insertInto() method generates bad grammar sql.
        assignSql.append("INSERT INTO CCFeederBankList (FeederID, DeviceID, ControlOrder, CloseOrder, TripOrder) ");
        assignSql.append("SELECT " + feederId + ", " + capbankId + ", MAX(ControlOrder) + 1,MAX(CloseOrder) + 1, 1");
        assignSql.append("FROM CCFeederBankList");
        assignSql.append("WHERE FeederID").eq(feederId);

        // remove any existing assignment
        unassignCapbank(capbank);

        // Check if any assignments exist.
        int rowsAffected = 0;

        SqlStatementBuilder countSql = new SqlStatementBuilder();

        countSql.append("SELECT COUNT(FeederID)");
        countSql.append("FROM CCFeederBankList");
        countSql.append("WHERE FeederID").eq(feederId);

        int count = yukonJdbcTemplate.queryForInt(countSql);

        if (count > 0) {
            // Change trip orders to +1 so the new one can be 1
            yukonJdbcTemplate.update(tripSql);

            // Insert new assignment
            rowsAffected = yukonJdbcTemplate.update(assignSql);
        } else {
            // This is the first assignment. Insert with defaults
            rowsAffected = yukonJdbcTemplate.update(insertSql);
        }

        boolean result = (rowsAffected == 1);

        if (result) {
            dbChangeManager.processPaoDbChange(capbank, DbChangeType.UPDATE);
            dbChangeManager.processPaoDbChange(feeder, DbChangeType.UPDATE);
        }

        return result;
    }
    
    @Override
    public boolean unassignCapbank(YukonPao capbank) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("DELETE FROM CCFeederBankList");
        sql.append("WHERE DeviceID").eq(capbank.getPaoIdentifier().getPaoId());

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    public PaoIdentifier findCapBankByCbc(int cbcId) {
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
    
    @Override
    public PaoIdentifier getParentBus(int bankId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT YPO2.PaObjectId, YPO2.Type");        
        sql.append("FROM CCFeederBankList FBL");
        sql.append("JOIN YukonPAObject YPO ON FBL.FeederID = YPO.PAObjectID");
        sql.append("JOIN CCFeederSubAssignment FSA ON FSA.FeederID = FBL.FeederID");
        sql.append("JOIN YukonPAObject YPO2 ON YPO2.PAObjectID = FSA.SubStationBusID");
        sql.append("WHERE FBL.DeviceID").eq(bankId);
        
        try {
            PaoIdentifier paoIdentifier = yukonJdbcTemplate.queryForObject(sql, RowMapper.PAO_IDENTIFIER);
            return paoIdentifier;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Parent Subbus was not found for CapBank with ID: " + bankId);
        }
    }   
}
