package com.cannontech.stars.core.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.EventInventoryDao;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMConfigurationBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class InventoryBaseDaoImpl implements InventoryBaseDao, InitializingBean {
    
    private static final Logger log = YukonLogManager.getLogger(InventoryBaseDaoImpl.class);
    
    private static final YukonRowMapper<LiteInventoryBase> smartInventoryRowMapper = new SmartLiteInventoryBaseRowMapper();
    private static final YukonRowMapper<InventoryBase> inventoryBaseRowMapper = new YukonRowMapper<InventoryBase>() {
        @Override
        public InventoryBase mapRow(YukonResultSet rs) throws SQLException {
            InventoryBase inventoryBase = new InventoryBase();
            inventoryBase.setAccountId(rs.getInt("AccountID"));
            inventoryBase.setAlternateTrackingNumber(SqlUtils.convertDbValueToString(rs.getString("AlternateTrackingNumber")));
            inventoryBase.setCategoryId(rs.getInt("CategoryID"));
            inventoryBase.setCurrentStateId(rs.getInt("CurrentStateID"));
            inventoryBase.setDeviceId(rs.getInt("DeviceID"));
            inventoryBase.setDeviceLabel(SqlUtils.convertDbValueToString(rs.getString("DeviceLabel")));
            inventoryBase.setInstallationCompanyId(rs.getInt("InstallationCompanyID"));
            inventoryBase.setInstallDate(new Timestamp(rs.getDate("InstallDate").getTime()));
            inventoryBase.setInventoryId(rs.getInt("InventoryID"));
            inventoryBase.setNotes(SqlUtils.convertDbValueToString(rs.getString("Notes")));
            inventoryBase.setReceiveDate(new Timestamp(rs.getDate("ReceiveDate").getTime()));
            inventoryBase.setRemoveDate(new Timestamp(rs.getDate("RemoveDate").getTime()));
            inventoryBase.setVoltageId(rs.getInt("VoltageID"));
            return inventoryBase;
        }
    };
    
    private SimpleTableAccessTemplate<LiteInventoryBase> liteInventoryTemplate;

    private static final String selectInventorySql;
    private static final String insertECToInventorySql;
    private static final String insertLmHardwareSql;
    private static final String insertMeterHardwareSql;
    private static final String updateLmHardwareSql;
    private static final String updateMeterHardwareSql;

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;    
    @Autowired private LMHardwareConfigurationDao lmHardwareConfigurationDao;
    @Autowired private ThermostatScheduleDao thermostatScheduleDao;
    @Autowired private LMHardwareEventDao hardwareEventDao;
    @Autowired private LMConfigurationBaseDao lmConfigurationBaseDao;
    @Autowired private EventInventoryDao eventInventoryDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private InventoryDao inventoryDao;

    static {
        selectInventorySql = "SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId "
            + "FROM InventoryBase ib "
            + "LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId "
            + "LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId "
            + "LEFT OUTER JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId "
            + "JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId ";

        insertECToInventorySql = "INSERT INTO ECToInventoryMapping (EnergyCompanyID,InventoryID) VALUES (?,?)";

        insertLmHardwareSql = "INSERT INTO LMHardwareBase (ManufacturerSerialNumber,LMHardwareTypeID," 
            + "RouteID,ConfigurationID,InventoryID) VALUES (?,?,?,?,?)";

        updateLmHardwareSql = "UPDATE LMHardwareBase SET ManufacturerSerialNumber = ?, LMHardwareTypeID = ?, RouteID = ?, " 
            + "ConfigurationID = ? WHERE InventoryID = ?";
        
        insertMeterHardwareSql = "INSERT INTO MeterHardwareBase (MeterNumber, MeterTypeID, InventoryID) VALUES (?,?,?)";
        
        updateMeterHardwareSql = "UPDATE MeterHardwareBase SET MeterNumber = ?, MeterTypeID = ? WHERE InventoryID = ?";

    }

    @Override
    public LiteInventoryBase getByInventoryId(final int inventoryId) throws NotFoundException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectInventorySql);
        sql.append("WHERE ib.InventoryId").eq(inventoryId);
        try {
            return yukonJdbcTemplate.queryForObject(sql, smartInventoryRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("LiteInventoryBase not found by Inventory Id: " + inventoryId);
        }
    }

    @Override
    public LiteLmHardwareBase getHardwareByInventoryId(int inventoryId) {
        return (LiteLmHardwareBase) getByInventoryId(inventoryId);
    }
    
    @Override
    public LiteLmHardwareBase getHardwareByDeviceId(int deviceId) {
        try {
            return (LiteLmHardwareBase) getByDeviceId(deviceId);
        } catch (ClassCastException e) {
            throw new NotFoundException("Inventory with device id: " + deviceId + " is not an LM hardware device.");
        }
    }

    @Override
    public Integer findMeterAssignment(int lmHardwareId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select MeterInventoryId");
        sql.append("from LMHardwareToMeterMapping");
        sql.append("where lmHardwareInventoryId ").eq(lmHardwareId);
        
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<DisplayableLmHardware> getLmHardwareForAccount(int accountId, HardwareClass lmHardwareClass) {
        Set<HardwareType> hardwareTypes = HardwareType.getForClass(lmHardwareClass);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT lm.ManufacturerSerialNumber serialNumber, ib.inventoryId inventoryId, ib.deviceLabel label");
        sql.append("FROM InventoryBase ib");
        sql.append("  JOIN LMHardwareBase lm on lm.InventoryID = ib.InventoryID");
        sql.append("  JOIN YukonListEntry yle on yle.EntryID = lm.LMHardwareTypeID");
        sql.append("WHERE AccountID ").eq(accountId);
        sql.append("  AND yle.YukonDefinitionID ").in(hardwareTypes);  
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<DisplayableLmHardware>(){
            @Override
            public DisplayableLmHardware mapRow(YukonResultSet rs) throws SQLException {
                DisplayableLmHardware hw = new DisplayableLmHardware();
                hw.setInventoryId(rs.getInt("inventoryId"));
                hw.setSerialNumber(rs.getString("serialNumber"));
                hw.setLabel(SqlUtils.convertDbValueToString(rs.getString("label")));
                return hw;
            }});
    }
    
    @Override
    public List<Integer> getSwitchAssignmentsForMeter(int meterId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select LMHardwareInventoryId");
        sql.append("from LMHardwareToMeterMapping");
        sql.append("where MeterInventoryId ").eq(meterId);
        
        return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getByDeviceId(final int deviceId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append(selectInventorySql);
    	sql.append("WHERE ib.DeviceId").eq(deviceId);
    	
        List<LiteInventoryBase> liteInventoryList = yukonJdbcTemplate.query(sql, smartInventoryRowMapper);
        
        if (liteInventoryList.size() == 0) {
            throw new NotFoundException("LiteInventoryBase not found by Device Id: " + deviceId);
        }
        
        if (liteInventoryList.size() > 1) {
            log.warn("Multiple inventory found for deviceId: " + deviceId + ", got " + liteInventoryList.size() + " rows.");
        }
        return liteInventoryList.get(0);
    }
    
    @Override
    public List<LiteInventoryBase> getByIds(final Collection<Integer> inventoryIds) {
        
        ChunkingSqlTemplate chunker = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        List<LiteInventoryBase> list = chunker.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectInventorySql);
                sql.append("WHERE ib.InventoryId").in(subList);
                return sql;
            }
        }, inventoryIds, smartInventoryRowMapper);
        
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, LiteInventoryBase> getByIdsMap(Collection<Integer> inventoryIds) {
        List<LiteInventoryBase> list = getByIds(inventoryIds);

        Map<Integer, LiteInventoryBase> map = new HashMap<Integer, LiteInventoryBase>(list.size());

        for (final LiteInventoryBase value : list) {
            Integer key = value.getInventoryID();
            map.put(key, value);
        }

        return map;
    }

    @Override
    public List<LiteLmHardwareBase> getAllLMHardware(Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany energyCompany : yecList) {
            ecIdList.add(energyCompany.getEnergyCompanyId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIdList);

        List<LiteLmHardwareBase> liteHardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());

        return liteHardwareList;
    }
    
    @Override
    public List<LiteLmHardwareBase> getLMHardwareForIds(Collection<Integer> inventoryIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE ib.InventoryId IN (").appendArgumentList(inventoryIds).append(")");

        List<LiteLmHardwareBase> liteHardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());

        return liteHardwareList;
    }

    @Override
    public List<LiteLmHardwareBase> getAllLMHardwareWithoutLoadGroups(Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany energyCompany : yecList) {
            ecIdList.add(energyCompany.getEnergyCompanyId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId, lhc.*");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN LMHardwareConfiguration lhc ON lhc.InventoryId = ib.InventoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIdList);
        sql.append("AND lhc.AddressingGroupId = 0");

        List<LiteLmHardwareBase> liteHardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());

        return liteHardwareList;
    }

    @Override
    public List<LiteInventoryBase> getAllMCTsWithAccount(Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany energyCompany : yecList) {
            ecIdList.add(energyCompany.getEnergyCompanyId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId IN").in(ecIdList);
        sql.append("AND ib.AccountId > 0");
        sql.append("AND yle.YukonDefinitionId").eq_k(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);

        List<LiteInventoryBase> liteHardwareList = yukonJdbcTemplate.query(sql, smartInventoryRowMapper);

        return liteHardwareList;

    }

    @Override
    public List<LiteInventoryBase> getAllMCTsWithoutAccount(Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany energyCompany : yecList) {
            ecIdList.add(energyCompany.getEnergyCompanyId());
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIdList);
        sql.append("AND ib.AccountId = 0");
        sql.append("AND yle.YukonDefinitionId").eq_k(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);

        List<LiteInventoryBase> liteHardwareList = yukonJdbcTemplate.query(sql, smartInventoryRowMapper);

        return liteHardwareList;

    }

    @Override
    public void updateInventoryBaseDeviceId(int inventoryId, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("UPDATE InventoryBase SET DeviceId").eq(deviceId);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void updateCurrentState(int inventoryId, int stateId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase SET CurrentStateId").eq(stateId);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int getDeviceStatus(int inventoryId) {
        
        List<LiteLMHardwareEvent> invHist = hardwareEventDao.getByInventoryId(inventoryId);
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        boolean isSA = inventory.getHardwareType().isSA();
        
        int completed = YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED;
        int config = YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG;
        int futureActivation = YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION;
        int tempTermination = YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION;
        
        int available = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
        int unavailable = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
        int tempUnavailable = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL;
        int termination = YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION;
        
        for (LiteLMHardwareEvent liteEvent : invHist) {
            int event = yukonListDao.getYukonListEntry(liteEvent.getActionID()).getYukonDefID();
            
            if (event == completed || isSA && event == config) {
                return available;
            }
            
            if (event == futureActivation || event == tempTermination) {
                return tempUnavailable;
            }
            
            if (event == termination) {
                return unavailable;
            }
        }
        
        return available;
    }
    
    @Override
    @Transactional
    public LiteInventoryBase saveInventoryBase(LiteInventoryBase liteInv, int energyCompanyId) {
        boolean insert = false;
        if (liteInv.getInventoryID() <= 0) {
            insert = true;
        }

        liteInventoryTemplate.save(liteInv);

        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] {energyCompanyId, liteInv.getInventoryID()};


        if (insert) {
            // Insert into ECToInventoryMapping
            yukonJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
        } else {
            // Update InventoryBase
            liteInventoryTemplate.update(liteInv);
        }

        return liteInv;
    }
    
    @Override
    @Transactional
    public LiteLmHardwareBase saveLmHardware(LiteLmHardwareBase lslmh, int energyCompanyId) {
        boolean insert = false;
        if (lslmh.getInventoryID() <= 0) {
            insert = true;
        }

        liteInventoryTemplate.save(lslmh);
        
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] {energyCompanyId, lslmh.getInventoryID()};

        // Insert into LMHardwareBase
        Object[] lmHwParams = new Object[] {lslmh.getManufacturerSerialNumber(), 
                lslmh.getLmHardwareTypeID(),
                lslmh.getRouteID(), 
                lslmh.getConfigurationID(),
                lslmh.getInventoryID()};

        
        if (insert) {
            // Insert into ECToInventoryMapping
            // Insert into LmHardwareBase
            yukonJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            yukonJdbcTemplate.update(insertLmHardwareSql, lmHwParams);
        } else {
            //Update LmHardareBase
            yukonJdbcTemplate.update(updateLmHardwareSql, lmHwParams);
        }

        return lslmh;
    }
    
    @Override
    @Transactional
    public LiteMeterHardwareBase saveMeterHardware(LiteMeterHardwareBase mhb, int ecId) {
        boolean insert = false;
        if (mhb.getInventoryID() <= 0) {
            insert = true;
        }

        liteInventoryTemplate.save(mhb);
        
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { ecId, mhb.getInventoryID() };

        // Insert into MeterHardwareBase
        Object[] meterHwParams = new Object[] {
                mhb.getMeterNumber(), mhb.getMeterTypeID(),
                mhb.getInventoryID() };

        
        if (insert) {
            // Insert into ECToInventoryMapping
            // Insert into MeterHardwareBase
            yukonJdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            yukonJdbcTemplate.update(insertMeterHardwareSql, meterHwParams);
        } else {
            // Update MeterHardwareBase           
            yukonJdbcTemplate.update(updateMeterHardwareSql, meterHwParams);
        }

        return mhb;
    }
    
    @Override
    @Transactional
    public void saveSwitchAssignments(Integer meterId, List<Integer> switchIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from LMHardwareToMeterMapping where MeterInventoryId ").eq(meterId);
        yukonJdbcTemplate.update(sql);
        
        for(int switchId : switchIds) {
            sql = new SqlStatementBuilder();
            sql.append("insert into LMHardwareToMeterMapping");
            sql.append("values(");
            sql.appendArgument(switchId).append(",");
            sql.appendArgument(meterId);
            sql.append(")");
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    public void removeInventoryFromAccount(int inventoryId, Instant removeDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase");
        sql.append("SET AccountId = 0, DeviceLabel = '', RemoveDate =").appendArgument(removeDate);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional
    public void deleteInventoryBase(int inventoryId) {

        // Retrieve the Inventory
        LiteInventoryBase liteInv = getByInventoryId(inventoryId);

        // Delete the lmHardware info
        deleteLMHardwareInfo(liteInv);

        // Now delete the Inventory base info
        eventInventoryDao.deleteInventoryEvents(inventoryId);
        ecMappingDao.deleteECToInventoryMapping(inventoryId);
        deleteInventoryToWarehouseMapping(inventoryId);
        internalDeleteInventoryBase(inventoryId);
    }
    
    @Override
    public List<PaoIdentifier> getPaosNotInInventory() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE NOT EXISTS");
        sql.append(  "(SELECT * FROM InventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId)");

        List<PaoIdentifier> paoList = yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());
        
        return paoList;
    }

    @Override
    public List<PaoIdentifier> getPaosNotOnAnAccount(Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIdList);
        sql.append("AND ib.AccountId = 0");
        sql.append("AND yle.YukonDefinitionId").eq_k(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);

        List<PaoIdentifier> paoList = yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());

        return paoList;
    }
    
    private void deleteLMHardwareInfo(LiteInventoryBase lib) {
        int inventoryId = lib.getInventoryID();

        lmHardwareConfigurationDao.delete(inventoryId);
        accountThermostatScheduleDao.deleteByInventoryId(inventoryId);
        thermostatScheduleDao.deleteManualEvents(inventoryId);
        
        if (lib instanceof LiteLmHardwareBase) {
            LiteLmHardwareBase lmHw = (LiteLmHardwareBase) lib;
            if (lmHw.getConfigurationID() > 0) {
                lmConfigurationBaseDao.delete(lmHw.getConfigurationID());
            }
        }
        deleteHardwareToMeterMapping(inventoryId);
        hardwareEventDao.deleteAllLMHardwareEvents(inventoryId);
        deleteLmHardware(inventoryId);
    }

    private void deleteHardwareToMeterMapping(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareToMeterMapping WHERE LMHardwareInventoryId").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }

    private void deleteLmHardware(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMHardwareBase WHERE InventoryID").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }

    private void deleteInventoryToWarehouseMapping(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM InventoryToWarehouseMapping WHERE InventoryID").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }

    private void internalDeleteInventoryBase(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM InventoryBase WHERE InventoryID").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public InventoryBase findById(int inventoryId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT InventoryID, AccountID, InstallationCompanyID, CategoryID, ReceiveDate,");
            sql.append(  "InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID, Notes, DeviceID,");
            sql.append(  "DeviceLabel, CurrentStateID");
            sql.append("FROM InventoryBase WHERE InventoryID").eq(inventoryId);
            
            return yukonJdbcTemplate.queryForObject(sql, inventoryBaseRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<Integer> getInventoryIdsByAccountId(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE AccountId").eq(accountId);
        List<Integer> inventoryIdList = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return inventoryIdList;
    }
    
    @Override
    public void afterPropertiesSet() {
        liteInventoryTemplate = new SimpleTableAccessTemplate<LiteInventoryBase>(yukonJdbcTemplate, nextValueHelper);
        liteInventoryTemplate.setTableName("InventoryBase");
        liteInventoryTemplate.setPrimaryKeyField("InventoryID");
        liteInventoryTemplate.setFieldMapper(inventoryFieldMapper);
        liteInventoryTemplate.setPrimaryKeyValidOver(0);
    }
    
    private FieldMapper<LiteInventoryBase> inventoryFieldMapper = new FieldMapper<LiteInventoryBase>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteInventoryBase o) {
            p.addValue("AccountID", o.getAccountID());
            p.addValue("InstallationCompanyID", o.getInstallationCompanyID());
            p.addValue("CategoryID", o.getCategoryID());
            p.addValue("ReceiveDate", new Timestamp(o.getReceiveDate()));
            p.addValue("InstallDate", new Timestamp(o.getInstallDate()));
            p.addValue("RemoveDate", new Timestamp(o.getRemoveDate()));
            p.addValue("AlternateTrackingNumber", SqlUtils.convertStringToDbValue(Strings.nullToEmpty(o.getAlternateTrackingNumber())));
            p.addValue("VoltageID", o.getVoltageID());
            p.addValue("Notes", SqlUtils.convertStringToDbValue(Strings.nullToEmpty(o.getNotes())));
            p.addValue("DeviceID", o.getDeviceID());
            p.addValue("DeviceLabel", o.getDeviceLabel());
            p.addValue("CurrentStateID", o.getCurrentStateID());
        }

        @Override
        public Number getPrimaryKey(LiteInventoryBase object) {
            return object.getInventoryID();
        }

        @Override
        public void setPrimaryKey(LiteInventoryBase object, int value) {
            object.setInventoryID(value);
        }
    };
    
}