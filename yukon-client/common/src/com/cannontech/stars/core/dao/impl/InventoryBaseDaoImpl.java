package com.cannontech.stars.core.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
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
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TypeRowMapper;
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
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Strings;

public class InventoryBaseDaoImpl implements InventoryBaseDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private LMHardwareEventDao hardwareEventDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private SelectionListService selectionListService;
    
    Logger log = YukonLogManager.getLogger(InventoryBaseDaoImpl.class);
    
    private final static YukonRowMapper<LiteInventoryBase> smartInventoryRowMapper = new SmartLiteInventoryBaseRowMapper();
    private final static YukonRowMapper<InventoryBase> inventoryBaseRowMapper = new YukonRowMapper<InventoryBase>() {
        @Override
        public InventoryBase mapRow(YukonResultSet rs) throws SQLException {
            InventoryBase inventoryBase = new InventoryBase();
            inventoryBase.setAccountId(rs.getInt("AccountID"));
            inventoryBase.setAlternateTrackingNumber(
                    SqlUtils.convertDbValueToString(rs.getString("AlternateTrackingNumber")));
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
    private FieldMapper<LiteInventoryBase> inventoryFieldMapper = new FieldMapper<LiteInventoryBase>() {
        
        @Override
        public void extractValues(MapSqlParameterSource p, LiteInventoryBase o) {
            p.addValue("AccountID", o.getAccountID());
            p.addValue("InstallationCompanyID", o.getInstallationCompanyID());
            p.addValue("CategoryID", o.getCategoryID());
            p.addValue("ReceiveDate", new Timestamp(o.getReceiveDate()));
            p.addValue("InstallDate", new Timestamp(o.getInstallDate()));
            p.addValue("RemoveDate", new Timestamp(o.getRemoveDate()));
            p.addValue("AlternateTrackingNumber", 
                    SqlUtils.convertStringToDbValue(Strings.nullToEmpty(o.getAlternateTrackingNumber())));
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
    
    private final static String selectInventorySql;
    private final static String insertECToInventorySql;
    private final static String insertLmHardwareSql;
    private final static String insertMeterHardwareSql;
    private final static String updateLmHardwareSql;
    private final static String updateMeterHardwareSql;
    
    static {
        selectInventorySql = "SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId "
            + "FROM InventoryBase ib "
            + "LEFT JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId "
            + "LEFT JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId "
            + "LEFT JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId "
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
            return jdbcTemplate.queryForObject(sql, smartInventoryRowMapper);
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
            return jdbcTemplate.queryForInt(sql);
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
        
        return jdbcTemplate.query(sql, new YukonRowMapper<DisplayableLmHardware>() {
            @Override
            public DisplayableLmHardware mapRow(YukonResultSet rs) throws SQLException {
                DisplayableLmHardware hw = new DisplayableLmHardware();
                hw.setInventoryId(rs.getInt("inventoryId"));
                hw.setSerialNumber(rs.getString("serialNumber"));
                hw.setLabel(SqlUtils.convertDbValueToString(rs.getString("label")));
                return hw;
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getByDeviceId(final int deviceId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectInventorySql);
        sql.append("WHERE ib.DeviceId").eq(deviceId);
        
        List<LiteInventoryBase> inventories = jdbcTemplate.query(sql, smartInventoryRowMapper);
        
        if (inventories.size() == 0) {
            throw new NotFoundException("LiteInventoryBase not found by Device Id: " + deviceId);
        }
        
        if (inventories.size() > 1) {
            log.warn("Multiple inventory found for deviceId: " + deviceId + ", got " 
                    + inventories.size() + " rows.");
        }
        return inventories.get(0);
    }
    
    @Override
    public List<LiteInventoryBase> getByIds(final Collection<Integer> inventoryIds) {
        
        ChunkingSqlTemplate chunker = new ChunkingSqlTemplate(jdbcTemplate);
        
        List<LiteInventoryBase> inventories = chunker.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectInventorySql);
                sql.append("WHERE ib.InventoryId").in(subList);
                return sql;
            }
        }, inventoryIds, smartInventoryRowMapper);
        
        return inventories;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Integer, LiteInventoryBase> getByIdsMap(Collection<Integer> inventoryIds) {
        
        List<LiteInventoryBase> inventories = getByIds(inventoryIds);
        
        Map<Integer, LiteInventoryBase> idToInventory = new HashMap<>(inventories.size());
        
        for (final LiteInventoryBase inventory : inventories) {
            idToInventory.put(inventory.getInventoryID(), inventory);
        }
        
        return idToInventory;
    }
    
    @Override
    public List<LiteLmHardwareBase> getAllLMHardware(Collection<YukonEnergyCompany> yecList) {
        
        List<Integer> ecIds = new ArrayList<>();
        for (YukonEnergyCompany energyCompany : yecList) {
            ecIds.add(energyCompany.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIds);
        
        List<LiteLmHardwareBase> hardware = jdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
        
        return hardware;
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
        
        List<LiteLmHardwareBase> hardware = jdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
        
        return hardware;
    }
    
    @Override
    public List<LiteLmHardwareBase> getAllLMHardwareWithoutLoadGroups(Collection<YukonEnergyCompany> companyies) {
        
        List<Integer> ecIds = new ArrayList<>();
        for (YukonEnergyCompany yec : companyies) {
            ecIds.add(yec.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId, lhc.*");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN LMHardwareConfiguration lhc ON lhc.InventoryId = ib.InventoryId");
        sql.append("WHERE etim.EnergyCompanyId").in(ecIds);
        sql.append("AND lhc.AddressingGroupId = 0");
        
        List<LiteLmHardwareBase> hardware = jdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
        
        return hardware;
    }
    
    @Override
    public void updateInventoryBaseDeviceId(int inventoryId, int deviceId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase SET DeviceId").eq(deviceId);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateCurrentState(int inventoryId, int stateId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase SET CurrentStateId").eq(stateId);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public int getDeviceStatus(int inventoryId) {
        
        List<LiteLMHardwareEvent> events = hardwareEventDao.getByInventoryId(inventoryId);
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
        
        for (LiteLMHardwareEvent event : events) {
            
            int type = yukonListDao.getYukonListEntry(event.getActionID()).getYukonDefID();
            
            if (type == completed || isSA && type == config) {
                return available;
            }
            
            if (type == futureActivation || type == tempTermination) {
                return tempUnavailable;
            }
            
            if (type == termination) {
                return unavailable;
            }
        }
        
        return available;
    }
    
    @Override
    @Transactional
    public LiteInventoryBase saveInventoryBase(LiteInventoryBase ib, int ecId) {
        
        boolean insert = ib.getInventoryID() <= 0;
        
        liteInventoryTemplate.save(ib);
        
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] {ecId, ib.getInventoryID()};
        
        if (insert) {
            // Insert into ECToInventoryMapping
            jdbcTemplate.update(insertECToInventorySql, ecToInvParams);
        } else {
            // Update InventoryBase
            liteInventoryTemplate.update(ib);
        }
        
        return ib;
    }
    
    @Override
    @Transactional
    public LiteLmHardwareBase saveLmHardware(LiteLmHardwareBase lmhb, int ecId) {
        
        boolean insert = lmhb.getInventoryID() <= 0;
        
        liteInventoryTemplate.save(lmhb);
        
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { ecId, lmhb.getInventoryID() };
        
        // Insert into LMHardwareBase
        Object[] lmHwParams = new Object[] {
            lmhb.getManufacturerSerialNumber(),
            lmhb.getLmHardwareTypeID(),
            lmhb.getRouteID(),
            lmhb.getConfigurationID(),
            lmhb.getInventoryID()
        };
        
        if (insert) {
            // Insert into ECToInventoryMapping
            // Insert into LmHardwareBase
            jdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            jdbcTemplate.update(insertLmHardwareSql, lmHwParams);
        } else {
            //Update LmHardareBase
            jdbcTemplate.update(updateLmHardwareSql, lmHwParams);
        }
        
        return lmhb;
    }
    
    @Override
    @Transactional
    public void addLmHardwareToMeterIfMissing(String manufacturerSerialNumber, int inventoryId, YukonEnergyCompany ec) {
        
        SqlStatementBuilder checkForLmHardwareSql = new SqlStatementBuilder();
        checkForLmHardwareSql.append("SELECT InventoryID");
        checkForLmHardwareSql.append("FROM LMHardwareBase");
        checkForLmHardwareSql.append("WHERE InventoryID").eq(inventoryId);
        
        try {
            // This will throw if it does not exist, which is a normal flow in this case
            jdbcTemplate.queryForInt(checkForLmHardwareSql);
        } catch(IncorrectResultSizeDataAccessException e) {
            // It is missing, so add this device to LMHardwareBase.
            
            YukonListEntry typeEntry = selectionListService.getListEntry(ec, HardwareType.YUKON_METER.getDefinitionId());
            int hardwareTypeId = typeEntry.getEntryID();
            
            Object[] lmHwParams = new Object[] {
                manufacturerSerialNumber,
                hardwareTypeId,
                0,
                0,
                inventoryId
            };
            
            jdbcTemplate.update(insertLmHardwareSql, lmHwParams);
        }
    } 
    
    @Override
    @Transactional
    public LiteMeterHardwareBase saveMeterHardware(LiteMeterHardwareBase mhb, int ecId) {
        
        boolean insert = mhb.getInventoryID() <= 0;
        
        liteInventoryTemplate.save(mhb);
        
        // Insert into ECToInventoryMapping
        Object[] ecToInvParams = new Object[] { ecId, mhb.getInventoryID() };
        
        // Insert into MeterHardwareBase
        Object[] meterHwParams = new Object[] {
            mhb.getMeterNumber(), 
            mhb.getMeterTypeID(),
            mhb.getInventoryID()
        };
        
        if (insert) {
            // Insert into ECToInventoryMapping
            // Insert into MeterHardwareBase
            jdbcTemplate.update(insertECToInventorySql, ecToInvParams);
            jdbcTemplate.update(insertMeterHardwareSql, meterHwParams);
        } else {
            // Update MeterHardwareBase
            jdbcTemplate.update(updateMeterHardwareSql, meterHwParams);
            
            // Note disconnect meters optionally have lmHardwareBase, but for now we are not updating it
            // during normal save or update operations. It is only used as a link for enrollment. and is 
            // managed as part of the enrollment workflow.
        }
        
        return mhb;
    }
    
    @Override
    @Transactional
    public void saveSwitchAssignments(Integer meterId, List<Integer> switchIds) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from LMHardwareToMeterMapping where MeterInventoryId ").eq(meterId);
        jdbcTemplate.update(sql);
        
        for(int switchId : switchIds) {
            sql = new SqlStatementBuilder();
            sql.append("insert into LMHardwareToMeterMapping");
            sql.append("values(");
            sql.appendArgument(switchId).append(",");
            sql.appendArgument(meterId);
            sql.append(")");
            jdbcTemplate.update(sql);
        }
    }
    
    @Override
    public void removeInventoryFromAccount(int inventoryId, Instant removeDate, String removeLbl) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase");
        sql.append("SET AccountId").eq_k(0);
        sql.append(", DeviceLabel").eq(removeLbl);
        sql.append(", RemoveDate").eq(removeDate);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<PaoIdentifier> getPaosNotInInventory() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonPAObject ypo");
        sql.append("WHERE NOT EXISTS");
        sql.append(  "(SELECT * FROM InventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId)");
        
        List<PaoIdentifier> paoList = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);
        
        return paoList;
    }
    
    @Override
    public InventoryBase findById(int inventoryId) {
        
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT InventoryID, AccountID, InstallationCompanyID, CategoryID, ReceiveDate,");
            sql.append(  "InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID, Notes, DeviceID,");
            sql.append(  "DeviceLabel, CurrentStateID");
            sql.append("FROM InventoryBase WHERE InventoryID").eq(inventoryId);
            
            return jdbcTemplate.queryForObject(sql, inventoryBaseRowMapper);
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
        List<Integer> inventoryIdList = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return inventoryIdList;
    }
    
    @PostConstruct
    public void init() {
        liteInventoryTemplate = new SimpleTableAccessTemplate<LiteInventoryBase>(jdbcTemplate, nextValueHelper);
        liteInventoryTemplate.setTableName("InventoryBase");
        liteInventoryTemplate.setPrimaryKeyField("InventoryID");
        liteInventoryTemplate.setFieldMapper(inventoryFieldMapper);
        liteInventoryTemplate.setPrimaryKeyValidOver(0);
    }
    
}