package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.InventoryIdentifierMapper;
import com.cannontech.common.inventory.LmHardwareInventoryIdentifierMapper;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.PagingExtractor;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLMHardwareEvent;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.DeviceAndPointValue;
import com.cannontech.stars.dr.hardware.model.HardwareStatus;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation class for InventoryDao
 */
public class InventoryDaoImpl implements InventoryDao {
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private InventoryIdentifierMapper identifierMapper;
    @Autowired private LMHardwareEventDao hardwareEventDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonListDao listDao;
    
    private ChunkingSqlTemplate chunkingSqlTemplate;
    
    private Set<HardwareType> THERMOSTAT_TYPES = HardwareType.getForClass(HardwareClass.THERMOSTAT);
    private HardwareSummaryRowMapper summaryRowMapper = new HardwareSummaryRowMapper();
    private MeterHardwareSummaryRowMapper meterSummaryRowMapper = new MeterHardwareSummaryRowMapper();
    
    @PostConstruct
    public void init() {
        chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
    }
    
    @Override
    public List<DeviceAndPointValue> getZigbeeProblemDevices(final String inWarehouseMsg) {
        
        AttributeDefinition definition =
            paoDefinitionDao.getAttributeLookup(PaoType.ZIGBEE_ENDPOINT, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        int pointOffset = definition.getPointTemplate().getOffset();
        PointType pointType = definition.getPointTemplate().getPointType();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Inventory.EnergyCompanyId, Inventory.AccountId, Inventory.InventoryID, Inventory.DeviceID, Inventory.ManufacturerSerialNumber, ");
        sql.append("Inventory.LMHardwareTypeID, Inventory.DeviceLabel, RPH1.POINTID, RPH1.TIMESTAMP, Inventory.PointType, RPH1.Value");
        sql.append("FROM RAWPOINTHISTORY RPH1,");
        
        sql.append("  (SELECT ECTA.EnergyCompanyID, IB.AccountId, IB.InventoryID, IB.DeviceID, HB.ManufacturerSerialNumber, ");
        sql.append("          IB.DeviceLabel, HB.LMHardwareTypeID, P.PointId, P.PointType");
        sql.append("   FROM InventoryBase IB");
        sql.append("     JOIN YukonPAObject YPO on YPO.PAObjectID = IB.DeviceID");
        sql.append("     JOIN LMHardwareBase HB on HB.InventoryID = IB.InventoryID");
        sql.append("     JOIN Point P on P.PAObjectID = YPO.PAObjectID");
        sql.append("     LEFT JOIN ECToAccountMapping ECTA on ECTA.AccountID = IB.AccountID");
        sql.append("   WHERE P.PointOffset").eq_k(pointOffset);
        sql.append("     AND P.PointType").eq_k(pointType);
        sql.append("     AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(PaoType.DIGIGATEWAY).append(
            ")");
        sql.append("  ) Inventory,");
        
        sql.append("  (SELECT PointId, MAX(timestamp) latestTime");
        sql.append("    FROM RawPointHistory rph");
        sql.append("    WHERE PointId in (");
        sql.append("      SELECT P.PointId FROM InventoryBase IB");
        sql.append("        JOIN YukonPAObject YPO on YPO.PAObjectID = IB.DeviceID");
        sql.append("        JOIN LMHardwareBase HB on HB.InventoryID = IB.InventoryID");
        sql.append("        JOIN Point P on P.PAObjectID = YPO.PAObjectID");
        sql.append("      WHERE P.PointOffset").eq_k(pointOffset);
        sql.append("        AND P.PointType").eq_k(pointType);
        sql.append("        AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(
            PaoType.DIGIGATEWAY).append(")");
        sql.append("    )");
        sql.append("    group by PointId");
        sql.append("  ) RPH2");
        
        sql.append("WHERE RPH1.PointId = RPH2.PointId");
        sql.append("  AND RPH1.timestamp = RPH2.latestTime");
        sql.append("  AND Inventory.PointId = RPH1.PointId");
        sql.append("  AND RPH1.Value").neq_k(Commissioned.CONNECTED.getRawState());
        
        sql.append("UNION");
        sql.append("(SELECT Inventory.EnergyCompanyID, Inventory.AccountID, Inventory.InventoryID, Inventory.DeviceID, Inventory.ManufacturerSerialNumber,");
        sql.append("Inventory.LMHardwareTypeID, Inventory.DeviceLabel, Inventory.POINTID, NULL as TIMESTAMP, Inventory.POINTTYPE, NULL as Value");
        sql.append("FROM");
        sql.append("   (SELECT ECTA.EnergyCompanyID, IB.AccountId, IB.InventoryID, IB.DeviceID, HB.ManufacturerSerialNumber, IB.DeviceLabel, HB.LMHardwareTypeID, P.PointId, P.PointType FROM InventoryBase IB");
        sql.append("      JOIN YukonPAObject YPO on YPO.PAObjectID = IB.DeviceID");
        sql.append("      JOIN LMHardwareBase HB on HB.InventoryID = IB.InventoryID");
        sql.append("      JOIN Point P on P.PAObjectID = YPO.PAObjectID");
        sql.append("      LEFT JOIN ECToAccountMapping ECTA on ECTA.AccountID = IB.AccountID");
        sql.append("    WHERE P.PointOffset").eq_k(pointOffset);
        sql.append("      AND P.PointType").eq_k(pointType);
        sql.append("      AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(PaoType.DIGIGATEWAY).append(
            ")");
        sql.append("   ) Inventory");
        sql.append("WHERE Inventory.POINTID not in (SELECT POINTID from RAWPOINTHISTORY))");
        
        return jdbcTemplate.query(sql, new YukonRowMapper<DeviceAndPointValue>() {
            @Override
            public DeviceAndPointValue mapRow(YukonResultSet rs) throws SQLException {
                
                LiteLmHardware hw = new LiteLmHardware();
                int inventoryId = rs.getInt("InventoryId");
                HardwareType type = getHardwareTypeById(rs.getInt("LMHardwareTypeId"));
                InventoryIdentifier id = new InventoryIdentifier(inventoryId, type);
                
                hw.setDeviceId(rs.getInt("DeviceId"));
                hw.setIdentifier(id);
                hw.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                hw.setAccountId(rs.getInt("AccountId"));
                // If a device doesn't belong to an account, the label shows an 'In Warehouse' message.
                if (rs.getInt("AccountId") == 0) {
                    hw.setLabel(inWarehouseMsg);
                } else {
                    hw.setLabel(rs.getString("DeviceLabel"));
                }
                hw.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
                
                int pointId = rs.getInt("PointId");
                Date timestamp = rs.getDate("Timestamp");
                int pointType = rs.getEnum("PointType", PointType.class).getPointTypeId();
                double pointValue = rs.getDouble("Value");
                // If there was no point in RPH, set this field to Disconnected.
                if (rs.wasNull()) {
                    pointValue = Commissioned.DISCONNECTED.getRawState();
                }
                SimplePointValue pvh = new SimplePointValue(pointId, timestamp, pointType, pointValue);
                
                return DeviceAndPointValue.of(hw, pvh);
            }
        });
    }
    
    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId, Set<HardwareType> types) {
            
        SqlStatementBuilder hardwareTypeSelection = new SqlStatementBuilder();
        hardwareTypeSelection.append("SELECT entryid");
        hardwareTypeSelection.append("FROM YukonListEntry");
        hardwareTypeSelection.append("WHERE YukonDefinitionID").in(types);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
        sql.append("lmhb.manufacturerSerialNumber,");
        sql.append("le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append("JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append("JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.accountID").eq(accountId);
        sql.append(" AND lmhb.LMHardwareTypeID").in(hardwareTypeSelection);
        
        List<HardwareSummary> hardwareList = jdbcTemplate.query(sql, summaryRowMapper);
        
        return hardwareList;
    }
    
    @Override
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account) {
        
        int accountId = account.getAccountId();
        return getThermostatsByAccountId(accountId);
    }
    
    @Override
    public List<Thermostat> getThermostatsByAccountId(int accountId) {
        
        Set<HardwareType> adjustable = HardwareType.getManualAdjustmentTypes();
        Set<HardwareType> schedulable = HardwareType.getSchedulableTypes();
        Set<HardwareType> supported = Sets.intersection(adjustable, schedulable);
        
        EnergyCompany ec = ecDao.getEnergyCompany(ecMappingDao.getEnergyCompanyIdForAccountId(accountId));
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lmhb.*");
        sql.append("FROM InventoryBase ib, LMHardwareBase lmhb");
        sql.append("WHERE ib.accountid").eq(accountId);
        sql.append("AND lmhb.inventoryid = ib.inventoryid");
        sql.append("AND lmhb.LMHardwareTypeID IN ");
        sql.append("(SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").in(THERMOSTAT_TYPES).append(")");
        sql.append("AND lmhb.LMHardwareTypeId IN ");
        sql.append("(SELECT entryId FROM YukonListEntry WHERE YukonDefinitionId").in(supported).append(")");
        List<Thermostat> thermostatList = jdbcTemplate.query(sql, new ThermostatRowMapper(ec));
        
        return thermostatList;
    }
    
    @Override
    public List<Thermostat> getThermostatsBySerialNumbers(EnergyCompany ec, Set<String> serialNumbers) {
        SqlFragmentGenerator<String> sqlGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.categoryId, ib.currentStateId, lmhb.manufacturerSerialNumber, lmhb.lmHardwareTypeId, lmhb.RouteId");
                sql.append("FROM InventoryBase ib");
                sql.append("JOIN LmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
                sql.append("JOIN ECToInventoryMapping ec ON ec.InventoryId = lmhb.InventoryId");
                sql.append("WHERE lmhb.manufacturerSerialNumber").in(subList);
                sql.append("AND ec.EnergyCompanyId").eq(ec.getId());
                sql.append("AND lmhb.LMHardwareTypeID IN ");
                sql.append("(SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").in(THERMOSTAT_TYPES).append(")");
                return sql;
                
            }
        };
        
        return chunkingSqlTemplate.query(sqlGenerator, serialNumbers, new ThermostatRowMapper(ec));
    }
    
    @Override
    public List<Thermostat> getNestThermostatsNotInListedAccounts(EnergyCompany ec, Set<String> accountNumbers) {
        SqlFragmentGenerator<String> sqlGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.categoryId, ib.currentStateId, lmhb.manufacturerSerialNumber, lmhb.lmHardwareTypeId, lmhb.RouteId");
                sql.append("FROM InventoryBase ib");
                sql.append("JOIN LmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
                sql.append("JOIN ECToInventoryMapping ec ON ec.InventoryId = lmhb.InventoryId");
                sql.append("JOIN CustomerAccount ca ON ca.accountID = ib.accountID");
                sql.append("WHERE ca.accountNumber").notIn(subList);
                sql.append("AND ec.EnergyCompanyId").eq(ec.getId());
                sql.append("AND lmhb.LMHardwareTypeID IN ");
                sql.append("(SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").eq(YukonDefinition.DEV_TYPE_NEST_THERMOSTAT.getDefinitionId()).append(")");
                return sql;
                
            }
        };
        
        return chunkingSqlTemplate.query(sqlGenerator, accountNumbers, new ThermostatRowMapper(ec));
    }
    
    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
        sql.append("lmhb.manufacturerSerialNumber,");
        sql.append("le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append("JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append("JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.accountID").eq(accountId);
        
        List<HardwareSummary> hardwareList = jdbcTemplate.query(sql, summaryRowMapper);
        
        return hardwareList;
    }
    
    @Override
    public List<HardwareSummary> getMeterHardwareSummaryForAccount(int accountId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
        sql.append("lmhb.manufacturerSerialNumber,");
        sql.append("le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append("LEFT JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append("LEFT JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("JOIN yukonListEntry le2 ON ib.categoryid = le2.entryId"); // Only used to filter
        sql.append("WHERE ib.accountID").eq(accountId);
        sql.append("AND le2.yukondefinitionid").eq_k(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
        
        List<HardwareSummary> hardwareList = jdbcTemplate.query(sql, meterSummaryRowMapper);
        
        return hardwareList;
    }
    
    @Override
    public HardwareSummary findHardwareSummaryById(int inventoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
        sql.append("lmhb.manufacturerSerialNumber,");
        sql.append("le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append("JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append("JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.InventoryID").eq(inventoryId);
        
        try {
            return jdbcTemplate.queryForObject(sql, summaryRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Map<Integer, HardwareSummary> findHardwareSummariesById(Iterable<Integer> inventoryIds) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
                sql.append("lmhb.manufacturerSerialNumber,");
                sql.append("le.yukonDefinitionId hardwareDefinitionId");
                sql.append("FROM inventoryBase ib");
                sql.append("JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
                sql.append("JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
                sql.append("WHERE ib.inventoryId").in(subList);
                return sql;
            }
        };
        
        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, HardwareSummary>> rowMapper =
            new YukonRowMapper<Entry<Integer, HardwareSummary>>() {
                @Override
                public Entry<Integer, HardwareSummary> mapRow(YukonResultSet rs) throws SQLException {
                    HardwareSummary hardware = summaryRowMapper.mapRow(rs);
                    int inventoryId = hardware.getInventoryId();
                    return Maps.immutableEntry(inventoryId, hardware);
                }
            };
            
        Map<Integer, HardwareSummary> retVal = template.mappedQuery(sqlGenerator, inventoryIds, rowMapper, typeMapper);
        
        return retVal;
    }
    
    @Override
    public List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel, ib.deviceid,");
        sql.append("lmhb.manufacturerSerialNumber,");
        sql.append("le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append("JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId ");
        sql.append("JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId ");
        sql.append("WHERE ib.accountID").eq(account.getAccountId());
        sql.append(" AND lmhb.LMHardwareTypeID IN ");
        sql.append(" (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").in(THERMOSTAT_TYPES).append(")");
        
        List<HardwareSummary> thermostatList = jdbcTemplate.query(sql, summaryRowMapper);
        
        return thermostatList;
    }
    
    @Override
    public Thermostat getThermostatById(int thermostatId) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(thermostatId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM InventoryBase IB");
        sql.append("JOIN LMHardwareBase LMHB ON LMHB.inventoryId = IB.inventoryId ");
        sql.append("WHERE IB.inventoryId").eq(thermostatId);
        
        Thermostat thermostat = jdbcTemplate.queryForObject(sql, new ThermostatRowMapper(ec));
        
        return thermostat;
    }
    
    @Override
    public void updateLabel(Thermostat thermostat, LiteYukonUser user) {
        
        int inventoryId = thermostat.getId();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceLabel FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        String oldLabel = jdbcTemplate.queryForString(sql);
        
        sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase");
        String label = thermostat.getDeviceLabel();
        sql.append("SET DeviceLabel").eq(label);
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        jdbcTemplate.update(sql);
        
        int accountId = getAccountIdForInventory(inventoryId);
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        String accountNo = customerAccount.getAccountNumber();
        String sn = thermostat.getSerialNumber();
        accountEventLogService.thermostatLabelChanged(user, accountNo, sn, oldLabel, label);
    }
    
    /** Mapper class to map a resultset row into a LiteHardware */
    private class HardwareSummaryRowMapper implements YukonRowMapper<HardwareSummary> {
        @Override
        public HardwareSummary mapRow(YukonResultSet rs) throws SQLException {
            
            int inventoryId = rs.getInt("InventoryID");
            String deviceLabel = rs.getString("DeviceLabel");
            String sn = rs.getString("ManufacturerSerialNumber");
            int hwDefinitionId = rs.getInt("hardwareDefinitionId");
            int deviceId = rs.getInt("DeviceId");
            HardwareType hardwareType = HardwareType.valueOf(hwDefinitionId);
            
            InventoryIdentifier identifier = new InventoryIdentifier(inventoryId, hardwareType);
            HardwareSummary hardware = new HardwareSummary(identifier, deviceLabel, sn, deviceId);
            
            return hardware;
        }
    }
    
    /** Mapper class to map a meters into HardwareSummary.
     * This deals with null values from meters that may not have a LMHardwareBase entry.*/
    private class MeterHardwareSummaryRowMapper implements YukonRowMapper<HardwareSummary> {
        @Override
        public HardwareSummary mapRow(YukonResultSet rs) throws SQLException {
            
            int inventoryId = rs.getInt("InventoryID");
            String deviceLabel = rs.getString("DeviceLabel");
            String sn = rs.getString("ManufacturerSerialNumber");
            Integer hwDefinitionId = rs.getNullableInt("hardwareDefinitionId");
            int deviceId = rs.getInt("DeviceId");
            
            // Meters without LMHardwareBase wouldn't have the serial number field yet.
            if(sn == null) {
                sn = deviceLabel;
            }
            
            // Meters may not have a LMHardwareBase entry, fake it as 0.
            if(hwDefinitionId == null) {
                hwDefinitionId = 0;
            }
            HardwareType hardwareType = HardwareType.valueOf(hwDefinitionId);
            
            InventoryIdentifier identifier = new InventoryIdentifier(inventoryId, hardwareType);
            HardwareSummary hardware = new HardwareSummary(identifier, deviceLabel, sn, deviceId);
            
            return hardware;
        }
    }
    
    /** Mapper class to map a ResultSet row into a thermostat */
    private class ThermostatRowMapper implements YukonRowMapper<Thermostat> {
        
        EnergyCompany energyCompany;
        
        public ThermostatRowMapper(EnergyCompany energyCompany) {
            this.energyCompany = energyCompany;
        }
        
        @Override
        public Thermostat mapRow(YukonResultSet rs) throws SQLException {
            
            Thermostat thermostat = new Thermostat();
            
            int id = rs.getInt("InventoryID");
            thermostat.setId(id);
            
            String serialNumber = rs.getString("ManufacturerSerialNumber");
            thermostat.setSerialNumber(serialNumber);
            
            String deviceLabel = rs.getString("DeviceLabel");
            thermostat.setDeviceLabel(deviceLabel);
            
            LiteStarsEnergyCompany liteStarsEnergyCompany = starsDbCache.getEnergyCompany(energyCompany.getId());
            
            // Convert the category entry id into a InventoryCategory enum value
            int categoryEntryId = rs.getInt("CategoryId");
            int categoryDefinitionId =
                YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                    YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY, categoryEntryId);
            
            InventoryCategory category = InventoryCategory.valueOf(categoryDefinitionId);
            thermostat.setCategory(category);
            
            // Convert the hardware type entry id into a HardwareType enum value
            int typeEntryId = rs.getInt("LMHardwareTypeId");
            int typeDefinitionId =
                YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                    YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, typeEntryId);
            HardwareType hardwareType = HardwareType.valueOf(typeDefinitionId);
            thermostat.setType(hardwareType);
            
            int routeId = rs.getInt("RouteId");
            if (routeId == CtiUtilities.NONE_ZERO_ID) {
                routeId = defaultRouteService.getDefaultRouteId(energyCompany);
            }
            thermostat.setRouteId(routeId);
            
            // Convert the current state entry id into a HardwareStatus enum value
            int statusEntryId = rs.getInt("CurrentStateId");
            HardwareStatus status = this.getStatus(statusEntryId, id, hardwareType);
            thermostat.setStatus(status);
            
            return thermostat;
        }
        
        /**
         * Helper method to determine the current status of a thermostat
         * 
         * @param statusEntryId - CurrentStateId in the InventoryBase table
         * @param thermostatId - Id of thermostat in question
         * @param type - Type of thermostat
         * @return - Status of thermostat
         */
        private HardwareStatus getStatus(int statusEntryId, int thermostatId, HardwareType type) {
            
            if (statusEntryId != 0) {
                int statusDefinitionId =
                    YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                        YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, statusEntryId);
                HardwareStatus status = HardwareStatus.valueOf(statusDefinitionId);
                return status;
            }
            
            boolean isSA = (type == HardwareType.SA_205) || (type == HardwareType.SA_305);
            
            List<LiteLMHardwareEvent> events = hardwareEventDao.getByInventoryId(thermostatId);
            for (LiteLMHardwareEvent event : events) {
                int actionId = event.getActionID();
                
                int actionDefinitionId =
                    YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                        YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_ACTION, actionId);
                
                if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED == actionDefinitionId || isSA
                    && YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG == actionDefinitionId) {
                    return HardwareStatus.AVAILABLE;
                }
                if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION == actionDefinitionId
                    || YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION == actionDefinitionId) {
                    return HardwareStatus.TEMP_UNAVAILABLE;
                }
                if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION == actionDefinitionId) {
                    return HardwareStatus.UNAVAILABLE;
                }
            }
            
            return HardwareStatus.AVAILABLE;
        }
    }
    
    @Override
    public InventoryIdentifier getYukonInventory(int inventoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
        sql.append("FROM InventoryBase I");
        sql.append("LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
        sql.append("LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
        sql.append("WHERE I.InventoryId").eq(inventoryId);
        
        return jdbcTemplate.queryForObject(sql, identifierMapper);
    }

    @Override
    public InventoryIdentifier getYukonInventoryForDeviceId(int deviceId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
        sql.append("FROM InventoryBase I");
        sql.append("LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
        sql.append("LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
        sql.append("WHERE I.DeviceId").eq(deviceId);
        
        return jdbcTemplate.queryForObject(sql, identifierMapper);
    }
    
    @Override
    public List<InventoryIdentifier> getYukonInventoryForDeviceIds(List<Integer> deviceIds) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
                sql.append("FROM InventoryBase I");
                sql.append("LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
                sql.append("LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
                sql.append("WHERE I.DeviceId").in(subList);
                return sql;
            }
        };
        
        List<InventoryIdentifier> hardware = chunkingSqlTemplate.query(sqlGenerator, deviceIds, identifierMapper);
        
        return hardware;
    }
    
    @Override
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds) {
        
        Set<InventoryIdentifier> result = new HashSet<>();
        
        SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
                sql.append("FROM InventoryBase I");
                sql.append("LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
                sql.append("LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
                sql.append("WHERE I.InventoryId").in(subList);
                return sql;
            }
        };
        chunkingSqlTemplate.queryInto(generator, inventoryIds, identifierMapper, result);
        
        return result;
    }
    
    @Override
    public InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT lmhb.inventoryId, yle.YukonDefinitionId");
        sql.append("FROM LMHardwareBase lmhb");
        sql.append("JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append("JOIN ECToInventoryMapping ec ON ec.InventoryId = lmhb.InventoryId");
        sql.append("WHERE lmhb.manufacturerSerialNumber").eq(serialNumber);
        sql.append("AND ec.EnergyCompanyId").eq(energyCompanyId);
        
        return jdbcTemplate.queryForObject(sql, new LmHardwareInventoryIdentifierMapper());
    }

    @Override
    public Map<String, Integer> getSerialNumberToInventoryIdMap(Collection<String> serialNumbers, int energyCompanyId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMHB.ManufacturerSerialNumber, LMHB.InventoryId");
        sql.append("FROM LMHardwareBase LMHB");
        sql.append("JOIN ECToInventoryMapping ECTIM ON ECTIM.InventoryId = LMHB.InventoryId");
        sql.append("WHERE LMHB.ManufacturerSerialNumber").in(serialNumbers);
        sql.append("AND ECTIM.EnergyCompanyId").eq_k(energyCompanyId);
        
        final Map<String, Integer> serialNumberToInventoryMap = Maps.newHashMapWithExpectedSize(serialNumbers.size());
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                String serialNumber = rs.getString("ManufacturerSerialNumber");
                int inventoryId = rs.getInt("InventoryId");
                serialNumberToInventoryMap.put(serialNumber, inventoryId);
            }
        });
        
        return serialNumberToInventoryMap;
    }
    
    @Override
    public List<Integer> getInventoryIds(Collection<String> serialNumbers, int energyCompanyId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMHB.InventoryId");
        sql.append("FROM LMHardwareBase LMHB");
        sql.append("JOIN ECToInventoryMapping ECTIM ON ECTIM.InventoryId = LMHB.InventoryId");
        sql.append("WHERE LMHB.ManufacturerSerialNumber").in(serialNumbers);
        sql.append("AND ECTIM.EnergyCompanyId").eq_k(energyCompanyId);
        
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
    
    @Override
    public List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> inventory) {
        
        DisplayableLmHardwareRowMapper mapper = new DisplayableLmHardwareRowMapper();
        
        Iterable<Integer> inventoryIds = Iterables.transform(inventory, YukonInventory.TO_INVENTORY_ID);
        
        SqlStatementBuilder sql = new SqlStatementBuilder(mapper.getBaseQuery().getSql());
        sql.append("WHERE lmhw.inventoryId").in(inventoryIds);
        sql.appendFragment(mapper.getOrderBy());
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    @Override
    public DisplayableLmHardware getDisplayableLMHardware(int inventoryId) {
        
        DisplayableLmHardwareRowMapper mapper = new DisplayableLmHardwareRowMapper();
        
        SqlStatementBuilder sql = new SqlStatementBuilder(mapper.getBaseQuery().getSql());
        sql.append("WHERE lmhw.inventoryId").eq(inventoryId);
        
        return jdbcTemplate.queryForObject(sql, mapper);
    }
    
    @Override
    public List<Integer> getInventoryIdsByAccount(int accountId) {
        
        List<Integer> inventoryIds = new ArrayList<>();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE AccountId").eq(accountId);
        inventoryIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return inventoryIds;
    }
    
    @Override
    public int getYukonDefinitionIdByEntryId(int entryId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YukonDefinitionId");
        sql.append("FROM YukonListEntry");
        sql.append("WHERE entryId").eq(entryId);
        int defId = jdbcTemplate.queryForInt(sql);
        
        return defId;
    }
    
    @Override
    public boolean checkAccountNumber(int inventoryId, String accountNumber) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.AccountNumber");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
        sql.append("WHERE ib.InventoryId").eq(inventoryId);
        
        String result = jdbcTemplate.queryForString(sql);
        
        return result.equalsIgnoreCase(accountNumber);
    }
    
    @Override
    public boolean checkdeviceType(int inventoryId, String deviceType) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT yle.EntryText");
        sql.append("FROM LmHardwareBase lmhb");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = lmhb.LmHardwareTypeId");
        sql.append("WHERE lmhb.InventoryId").eq(inventoryId);
        
        String result = jdbcTemplate.queryForString(sql);
        
        return result.equalsIgnoreCase(deviceType);
    }
    
    @Override
    public int getDeviceId(int inventoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public Map<Integer, Integer> getDeviceIds(Iterable<Integer> inventoryIds) {
        
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT InventoryId, DeviceId");
                sql.append("FROM InventoryBase");
                sql.append("WHERE InventoryId").in(subList);
                return sql;
            }
        };
        
        Map<Integer, Integer> inventoryIdToDeviceId = new HashMap<>();
        
        chunkingSqlTemplate.query(sqlFragmentGenerator, inventoryIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int inventoryId = rs.getInt("InventoryId");
                int deviceId = rs.getInt("DeviceId");
                inventoryIdToDeviceId.put(inventoryId, deviceId);
            }
        });
        
        return inventoryIdToDeviceId;
    }
    
    @Override
    public int getAccountIdForInventory(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AccountId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public HardwareType getHardwareTypeById(int hardwareTypeId) {
        Integer hardwareTypeDefinitionId;
        if (hardwareTypeId > 0) {
            /* This is a stars object that will live in either LMHardwareBase or MeterHardwareBase */
            YukonListEntry entry = listDao.getYukonListEntry(hardwareTypeId);
            hardwareTypeDefinitionId = entry.getYukonDefID();
        } else {
            /*
             * This is a real MCT that exists as a yukon pao. It will only have a row in InventoryBase
             * so there is no type id.
             */
            hardwareTypeDefinitionId = 0;
        }
        
        return HardwareType.valueOf(hardwareTypeDefinitionId);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SearchResults<InventorySearchResult> search(InventorySearch inventorySearch, Collection<Integer> ecIds,
            int start, int pageCount, final boolean starsMeters) {
        // Trim fields to null
        inventorySearch.trimFields();

        final boolean usePhone = StringUtils.isNotBlank(inventorySearch.getPhoneNumber());
        final boolean useWorkOrder = StringUtils.isNotBlank(inventorySearch.getWorkOrderNumber());

        SearchResults<InventorySearchResult> results = new SearchResults<>();

        SqlStatementBuilder selectCount = new SqlStatementBuilder();
        selectCount.append("SELECT COUNT(*)");

        SqlStatementBuilder selectData = new SqlStatementBuilder();
        selectData.append("SELECT IB.InventoryId,");
        selectData.append("IB.DeviceId,");
        selectData.append("LMHB.ManufacturerSerialNumber,");
        if (starsMeters) {
            selectData.append("MHB.MeterNumber,");
            selectData.append("MHB.MeterTypeID,");
        } else {
            selectData.append("DMG.MeterNumber,");
            selectData.append("YPO.Type,");
        }
        selectData.append("IM.EnergyCompanyId,");
        selectData.append("EC.Name EnergyCompanyName,");
        selectData.append("LMHB.LMHardwareTypeID,");
        selectData.append("IB.DeviceLabel,");
        selectData.append("CA.AccountId,");
        selectData.append("CA.AccountNumber,");
        selectData.append("CON.ContLastName,");
        if (usePhone) {
            selectData.append("CN.Notification,");
        }
        if (useWorkOrder) {
            selectData.append("WOB.OrderNumber,");
        }
        selectData.append("IB.AlternateTrackingNumber");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM InventoryBase IB");
        sql.append("JOIN ECToInventoryMapping IM on IM.InventoryID = IB.InventoryID");
        sql.append("JOIN EnergyCompany EC on EC.EnergyCompanyID = IM.EnergyCompanyID");
        sql.append("LEFT JOIN LMHardwareBase LMHB on LMHB.InventoryID = IB.InventoryID");
        if (starsMeters) {
            sql.append("LEFT JOIN MeterHardwareBase MHB on MHB.InventoryID = IB.InventoryID");
        } else {
            sql.append("LEFT JOIN YukonPAObject YPO on YPO.PAObjectId = IB.DeviceID");
            sql.append("LEFT JOIN DeviceMeterGroup DMG on DMG.DeviceId = IB.DeviceID");
        }
        sql.append("JOIN CustomerAccount CA on CA.AccountID = IB.AccountID");
        sql.append("JOIN AccountSite ACS on ACS.AccountSiteID = CA.AccountSiteID");
        sql.append("JOIN Customer CUS on CA.CustomerID = CUS.CustomerID");
        sql.append("JOIN Contact CON on CON.ContactID = CUS.PrimaryContactID");
        if (usePhone) {
            sql.append("JOIN ContactNotification CN ON CN.ContactID = CON.ContactID AND CN.NotificationCategoryID = 5");
        }
        if (useWorkOrder) {
            sql.append("JOIN WorkOrderBase WOB on WOB.AccountID = CA.AccountID");
        }
        sql.append("WHERE");
        
        // Where clause
        SqlFragmentCollection whereClause = SqlFragmentCollection.newAndCollection();
        whereClause.add(new SqlStatementBuilder("IM.EnergyCompanyId").in(ecIds));
        
        /* Search Parameters */
        if (StringUtils.isNotBlank(inventorySearch.getSerialNumber())) {
            whereClause.add(new SqlStatementBuilder("LMHB.ManufacturerSerialNumber").startsWith(inventorySearch.getSerialNumber()));
        }
        if (StringUtils.isNotBlank(inventorySearch.getMeterNumber())) {
            if (starsMeters) {
                whereClause.add(new SqlStatementBuilder("MHB.MeterNumber").startsWith(inventorySearch.getMeterNumber()));
            } else {
                whereClause.add(new SqlStatementBuilder("DMG.MeterNumber").startsWith(inventorySearch.getMeterNumber()));
            }
        }
        if (StringUtils.isNotBlank(inventorySearch.getAccountNumber())) {
            whereClause.add(new SqlStatementBuilder("CA.AccountNumber").startsWith(inventorySearch.getAccountNumber()));
        }
        if (usePhone) {
            String formattedPhoneNumber = "";
            try {
                formattedPhoneNumber = ServletUtils.formatPhoneNumberForSearch(inventorySearch.getPhoneNumber());
            } catch (WebClientException e) {
                // the phone# should be validated before this method is called.
            }
            SqlFragmentCollection orClause = SqlFragmentCollection.newOrCollection();
            orClause.add(new SqlStatementBuilder("CN.Notification").contains(inventorySearch.getPhoneNumber()));
            orClause.add(new SqlStatementBuilder("CN.Notification").contains(formattedPhoneNumber));
            whereClause.add(orClause);
        }
        if (StringUtils.isNotBlank(inventorySearch.getLastName())) {
            whereClause.add(new SqlStatementBuilder("CON.ContLastName").startsWith(inventorySearch.getLastName()));
        }
        if (useWorkOrder) {
            whereClause.add(new SqlStatementBuilder("WOB.OrderNumber").startsWith(inventorySearch.getWorkOrderNumber()));
        }
        if (StringUtils.isNotBlank(inventorySearch.getAltTrackingNumber())) {
            whereClause.add(new SqlStatementBuilder("IB.AlternateTrackingNumber").startsWith(inventorySearch.getAltTrackingNumber()));
        }
        sql.append(whereClause);
        
        int total = jdbcTemplate.queryForInt(selectCount.append(sql));
        
        YukonRowMapper<InventorySearchResult> mapper = new YukonRowMapper<InventorySearchResult>() {
            @Override
            public InventorySearchResult mapRow(YukonResultSet rs) throws SQLException {
                InventorySearchResult result = new InventorySearchResult();
                InventoryIdentifier identifier = getYukonInventory(rs.getInt("InventoryId"));
                result.setIdentifier(identifier);
                result.setDeviceId(rs.getInt("DeviceId"));
                result.setAccountId(rs.getInt("AccountId"));
                result.setAccountNumber(rs.getString("AccountNumber"));
                result.setAltTrackingNumber(rs.getString("AlternateTrackingNumber"));
                result.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
                result.setEnergyCompanyName(rs.getString("EnergyCompanyName"));
                result.setLabel(rs.getString("DeviceLabel"));
                result.setLastName(rs.getString("ContLastName"));
                if (usePhone) {
                    result.setPhoneNumber(rs.getString("Notification"));
                }
                if (identifier.getHardwareType().isMeter()) {
                    result.setSerialNumber(rs.getString("MeterNumber"));
                    if (!starsMeters) {
                        result.setPaoType(rs.getEnum("Type", PaoType.class));
                    }
                } else {
                    result.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                }
                if (useWorkOrder) {
                    result.setWorkOrderNumber(rs.getString("OrderNumber"));
                }

                return result;
            }
        };
        PagingExtractor<InventorySearchResult> extractor = new PagingExtractor<>(start, pageCount, mapper);
        List<InventorySearchResult> resultList =
            (List<InventorySearchResult>) jdbcTemplate.query(selectData.append(sql), extractor);
        
        results.setResultList(resultList);
        results.setBounds(start, pageCount, total);
        
        return results;
    }
    
    @Override
    public int getCategoryIdForTypeId(int hardwareTypeId, YukonEnergyCompany ec) {
        
        HardwareType hardwareType = getHardwareTypeById(hardwareTypeId);
        
        /* The category id is the entry id of the yukon list entry for this category in this energy company. */
        int categoryDefId = hardwareType.getInventoryCategory().getDefinitionId();
        YukonListEntry categoryEntryOfEnergyCompany = selectionListService.getListEntry(ec, categoryDefId);
        return categoryEntryOfEnergyCompany.getEntryID();
    }
    
    @Override
    public List<String> getThermostatLabels(List<Integer> thermostatIds) {
        List<String> deviceLabels = chunkingSqlTemplate.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT IB.DeviceLabel");
                sql.append("FROM InventoryBase IB");
                sql.append("WHERE IB.inventoryId").in(subList);
                
                return sql;
            }
        }, thermostatIds, TypeRowMapper.STRING);
        
        return deviceLabels;
    }
    
    @Override
    public String getMeterNumberForDevice(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MeterNumber");
        sql.append("FROM DeviceMeterGroup");
        sql.append("WHERE Deviceid").eq(deviceId);
        return jdbcTemplate.queryForString(sql);
    }
    
    @Override
    public LiteLmHardware getLiteLmHardwareByInventory(final YukonInventory inventory) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT IB.InventoryId, IB.DeviceId, LMHB.ManufacturerSerialNumber, IB.DeviceLabel, ECIM.EnergyCompanyId, IB.AccountId, CA.AccountNumber");
        sql.append("FROM InventoryBase IB");
        sql.append("JOIN LmHardwareBase LMHB on LMHB.InventoryId = IB.InventoryId");
        sql.append("JOIN EcToInventoryMapping ECIM on ECIM.InventoryId = IB.InventoryId");
        sql.append("JOIN CustomerAccount CA on CA.AccountId = IB.AccountId");
        sql.append("WHERE IB.InventoryId").eq(inventory.getInventoryIdentifier().getInventoryId());
        
        return jdbcTemplate.queryForObject(sql, new YukonRowMapper<LiteLmHardware>() {
            
            @Override
            public LiteLmHardware mapRow(YukonResultSet rs) throws SQLException {
                LiteLmHardware lmh = new LiteLmHardware();
                lmh.setIdentifier(inventory.getInventoryIdentifier());
                lmh.setDeviceId(rs.getInt("DeviceId"));
                lmh.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                lmh.setLabel(rs.getString("DeviceLabel"));
                lmh.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
                lmh.setAccountId(rs.getInt("AccountId"));
                lmh.setAccountNo(rs.getString("AccountNumber"));
                
                return lmh;
            }
            
        });
    }
    
    @Override
    public List<LiteLmHardware> getLiteLmHardwareByPaos(final List<? extends YukonPao> paos) {
        
        final List<Integer> paoIds = Lists.transform(paos, YukonPao.TO_PAO_ID);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT IB.InventoryId, IB.DeviceId, LMHB.ManufacturerSerialNumber, IB.DeviceLabel, ECIM.EnergyCompanyId, IB.AccountId, CA.AccountNumber, LMHB.LmHardwareTypeId");
                sql.append("FROM InventoryBase IB");
                sql.append("JOIN LmHardwareBase LMHB on LMHB.InventoryId = IB.InventoryId");
                sql.append("JOIN EcToInventoryMapping ECIM on ECIM.InventoryId = IB.InventoryId");
                sql.append("JOIN CustomerAccount CA on CA.AccountId = IB.AccountId");
                sql.append("WHERE IB.DeviceId").in(subList);
                return sql;
            }
        };
        
        List<LiteLmHardware> hardware =
            chunkingSqlTemplate.query(sqlGenerator, paoIds, new YukonRowMapper<LiteLmHardware>() {
                
                @Override
                public LiteLmHardware mapRow(YukonResultSet rs) throws SQLException {
                    LiteLmHardware lmh = new LiteLmHardware();
                    lmh.setIdentifier(new InventoryIdentifier(rs.getInt("InventoryId"),
                        getHardwareTypeById(rs.getInt("LMHardwareTypeId"))));
                    lmh.setDeviceId(rs.getInt("DeviceId"));
                    lmh.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                    lmh.setLabel(rs.getString("DeviceLabel"));
                    lmh.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
                    lmh.setAccountId(rs.getInt("AccountId"));
                    lmh.setAccountNo(rs.getString("AccountNumber"));
                    
                    return lmh;
                }
                
            });
        
        return hardware;
    }
    
    @Override
    public boolean accountMeterWarehouseIsNotEmpty(Set<Integer> ecId, boolean includeMctsWithNoAccount) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.STARS_ACCOUNT_ATTACHABLE_METER);
        paoTypes.addAll(PaoType.getRfMeterTypes());

        //This sql replicates how the device picker populates it's pickerList.
        
        //This first limiter will limit the devices that do not have an inventory associated.
        SqlStatementBuilder limiter1 = new SqlStatementBuilder();
        limiter1.append("paobjectId IN (");
        limiter1.append("  SELECT paobjectId");
        limiter1.append("  FROM YukonPAObject ypo");
        limiter1.append("  WHERE ypo.type ").in(paoTypes);
        limiter1.append("    AND ypo.paobjectId NOT IN (SELECT deviceId FROM inventoryBase ib WHERE ib.DeviceId = ypo.PAObjectId) )");

        //This limiter will add in mct devices to the picker list that have no account associated with it.
        SqlStatementBuilder limiter2 = new SqlStatementBuilder();
        limiter2.append("paobjectId IN (");
        limiter2.append("  SELECT deviceId");
        limiter2.append("  FROM inventoryBase ib ");
        limiter2.append("    JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        limiter2.append("    JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        limiter2.append("    JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        limiter2.append("  WHERE etim.EnergyCompanyId ").in(ecId);
        limiter2.append("    AND ib.accountId = 0 ");
        limiter2.append("    AND yle.YukonDefinitionId = ").append(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).append(")");
        
        SqlFragmentCollection whereClause = SqlFragmentCollection.newOrCollection();
        whereClause.add(limiter1);
        if (includeMctsWithNoAccount) {
            whereClause.add(limiter2);
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM yukonPAObject ");
        String filterSql = whereClause.getSql();
        if (!StringUtils.isEmpty(filterSql)) {
            sql.append("WHERE");
            sql.append(whereClause);
        }
        return jdbcTemplate.queryForInt(sql) > 0;
    }

}