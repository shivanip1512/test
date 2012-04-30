package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

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
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingExtractor;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareStatus;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.stars.util.InventoryUtils;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

/**
 * Implementation class for InventoryDao
 */
public class InventoryDaoImpl implements InventoryDao {

    private @Autowired ECMappingDao ecMappingDao;
    private @Autowired YukonEnergyCompanyService yukonEnergyCompanyService;
    private @Autowired LMHardwareEventDao hardwareEventDao;
    private @Autowired StarsDatabaseCache starsDatabaseCache;
    private @Autowired YukonJdbcTemplate yukonJdbcTemplate;
    private @Autowired InventoryIdentifierMapper inventoryIdentifierMapper;
    private @Autowired YukonListDao yukonListDao;
    private @Autowired PaoDefinitionDao paoDefinitionDao;
    private @Autowired AccountEventLogService accountEventLogService;
    private @Autowired CustomerAccountDao customerAccountDao;
    
    private Set<HardwareType> THERMOSTAT_TYPES = HardwareType.getForClass(HardwareClass.THERMOSTAT);
    private HardwareSummaryRowMapper hardwareSummaryRowMapper = new HardwareSummaryRowMapper();

    @Override
    public List<Pair<LiteLmHardware, SimplePointValue>> getZigbeeProblemDevices(final String inWarehouseMsg) {
        AttributeDefinition definition = (AttributeDefinition) paoDefinitionDao.getAttributeLookup(PaoType.ZIGBEE_ENDPOINT, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        int pointOffset = definition.getPointTemplate().getOffset();
        PointType pointType = definition.getPointTemplate().getPointType();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Inventory.EnergyCompanyId, Inventory.AccountId, Inventory.InventoryID, Inventory.ManufacturerSerialNumber, ");
        sql.append("Inventory.LMHardwareTypeID, Inventory.DeviceLabel, RPH1.POINTID, RPH1.TIMESTAMP, Inventory.PointType, RPH1.Value");
        sql.append("FROM RAWPOINTHISTORY RPH1,");
        
        sql.append("  (SELECT ECTA.EnergyCompanyID, IB.AccountId, IB.InventoryID, HB.ManufacturerSerialNumber, ");
        sql.append("          IB.DeviceLabel, HB.LMHardwareTypeID, P.PointId, P.PointType");
        sql.append("   FROM InventoryBase IB");
        sql.append("     JOIN YukonPAObject YPO on YPO.PAObjectID = IB.DeviceID");
        sql.append("     JOIN LMHardwareBase HB on HB.InventoryID = IB.InventoryID");
        sql.append("     JOIN Point P on P.PAObjectID = YPO.PAObjectID");
        sql.append("     LEFT JOIN ECToAccountMapping ECTA on ECTA.AccountID = IB.AccountID");
        sql.append("   WHERE P.PointOffset").eq_k(pointOffset);
        sql.append("     AND P.PointType").eq_k(pointType);
        sql.append("     AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(PaoType.DIGIGATEWAY).append(")");
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
        sql.append("        AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(PaoType.DIGIGATEWAY).append(")");
        sql.append("    )");
        sql.append("    group by PointId");
        sql.append("  ) RPH2");
        
        sql.append("WHERE RPH1.PointId = RPH2.PointId");
        sql.append("  AND RPH1.timestamp = RPH2.latestTime");
        sql.append("  AND Inventory.PointId = RPH1.PointId");
        sql.append("  AND RPH1.Value").neq_k(Commissioned.CONNECTED.getRawState());
        
        sql.append("UNION");
        sql.append("(SELECT Inventory.EnergyCompanyID, Inventory.AccountID, Inventory.InventoryID, Inventory.ManufacturerSerialNumber,");
        sql.append("Inventory.LMHardwareTypeID, Inventory.DeviceLabel, Inventory.POINTID, NULL as TIMESTAMP, Inventory.POINTTYPE, NULL as Value");
        sql.append("FROM");
        sql.append("   (SELECT ECTA.EnergyCompanyID, IB.AccountId, IB.InventoryID, HB.ManufacturerSerialNumber, IB.DeviceLabel, HB.LMHardwareTypeID, P.PointId, P.PointType FROM InventoryBase IB");
        sql.append("      JOIN YukonPAObject YPO on YPO.PAObjectID = IB.DeviceID");
        sql.append("      JOIN LMHardwareBase HB on HB.InventoryID = IB.InventoryID");
        sql.append("      JOIN Point P on P.PAObjectID = YPO.PAObjectID");
        sql.append("      LEFT JOIN ECToAccountMapping ECTA on ECTA.AccountID = IB.AccountID");
        sql.append("    WHERE P.PointOffset").eq_k(pointOffset);
        sql.append("      AND P.PointType").eq_k(pointType);
        sql.append("      AND (YPO.type").eq_k(PaoType.ZIGBEE_ENDPOINT).append("OR YPO.TYPE").eq_k(PaoType.DIGIGATEWAY).append(")");
        sql.append("   ) Inventory");
        sql.append("WHERE Inventory.POINTID not in (SELECT POINTID from RAWPOINTHISTORY))");
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<Pair<LiteLmHardware, SimplePointValue>>() {
            @Override
            public Pair<LiteLmHardware, SimplePointValue> mapRow(YukonResultSet rs) throws SQLException {
                LiteLmHardware hw = new LiteLmHardware();
                InventoryIdentifier id = new InventoryIdentifier(rs.getInt("InventoryId"), getHardwareTypeById(rs.getInt("LMHardwareTypeId")));
                hw.setIdentifier(id);
                hw.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                hw.setAccountId(rs.getInt("AccountId"));
                // If a device doesn't belong to an account, the label shows an 'In  Warehouse' message.
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
                
                return new Pair<LiteLmHardware, SimplePointValue>(hw, pvh);
            }
        });
    }
    
    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId, Set<HardwareType> hardwareTypes) {
        
        SqlStatementBuilder hardwareTypeSelection = new SqlStatementBuilder();
        hardwareTypeSelection.append("SELECT entryid");
        hardwareTypeSelection.append("FROM YukonListEntry");
        hardwareTypeSelection.append("WHERE YukonDefinitionID").in(hardwareTypes);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        sql.append(    "lmhb.manufacturerSerialNumber,");
        sql.append(    "le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.accountID").eq(accountId);
        sql.append(" AND lmhb.LMHardwareTypeID").in(hardwareTypeSelection);
        
        List<HardwareSummary> hardwareList = yukonJdbcTemplate.query(sql, hardwareSummaryRowMapper);
        
        return hardwareList;
    }
    
    @Override
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account) {
    	
    	int accountId = account.getAccountId();
    	return getThermostatsByAccountId(accountId);
    }
    
    @Override
    public List<Thermostat> getThermostatsByAccountId(int accountId) {
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lmhb.*");
        sql.append("FROM InventoryBase ib, LMHardwareBase lmhb");
        sql.append("WHERE ib.accountid").eq(accountId);
        sql.append(  "AND lmhb.inventoryid = ib.inventoryid");
        sql.append(  "AND lmhb.LMHardwareTypeID IN ");
        sql.append(    "(SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").in(THERMOSTAT_TYPES).append(")");
        List<Thermostat> thermostatList = yukonJdbcTemplate.query(sql, new ThermostatRowMapper(energyCompany));
        return thermostatList;
    }

    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        sql.append(    "lmhb.manufacturerSerialNumber,");
        sql.append(    "le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.accountID").eq(accountId);

        List<HardwareSummary> hardwareList = yukonJdbcTemplate.query(sql, hardwareSummaryRowMapper);

        return hardwareList;
    }
    
    @Override
    public HardwareSummary findHardwareSummaryById(int inventoryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        sql.append(    "lmhb.manufacturerSerialNumber,");
        sql.append(    "le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
        sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
        sql.append("WHERE ib.InventoryID").eq(inventoryId);

        try {
            return yukonJdbcTemplate.queryForObject(sql, hardwareSummaryRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }    

    @Override
    public Map<Integer, HardwareSummary> findHardwareSummariesById(Iterable<Integer> inventoryIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
                sql.append(    "lmhb.manufacturerSerialNumber,");
                sql.append(    "le.yukonDefinitionId hardwareDefinitionId");
                sql.append("FROM inventoryBase ib");
                sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId");
                sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId");
                sql.append("WHERE ib.inventoryId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, HardwareSummary>> rowMapper = new YukonRowMapper<Entry<Integer,HardwareSummary>>() {
            @Override
            public Entry<Integer, HardwareSummary> mapRow(YukonResultSet rs) throws SQLException {
                HardwareSummary hardware = hardwareSummaryRowMapper.mapRow(rs);
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
        sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        sql.append(    "lmhb.manufacturerSerialNumber,");
        sql.append(    "le.yukonDefinitionId hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId ");
        sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId ");
        sql.append("WHERE ib.accountID").eq(account.getAccountId());
        sql.append(" AND lmhb.LMHardwareTypeID IN ");
        sql.append(" (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID").in(THERMOSTAT_TYPES).append(")");

        List<HardwareSummary> thermostatList = yukonJdbcTemplate.query(sql, hardwareSummaryRowMapper);
        
        return thermostatList;
    }
    
    @Override
    public Thermostat getThermostatById(int thermostatId) {

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(thermostatId);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM InventoryBase IB");
        sql.append("JOIN LMHardwareBase LMHB ON LMHB.inventoryId = IB.inventoryId ");
        sql.append("WHERE IB.inventoryId").eq(thermostatId);

        Thermostat thermostat = yukonJdbcTemplate.queryForObject(sql, new ThermostatRowMapper(yukonEnergyCompany));
        return thermostat;
    }

    @Override
    public void updateLabel(Thermostat thermostat, LiteYukonUser user) {
        int inventoryId = thermostat.getId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceLabel FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        String oldThermostatLabel = yukonJdbcTemplate.queryForString(sql);

        sql = new SqlStatementBuilder();
        sql.append("UPDATE InventoryBase");
        sql.append("SET DeviceLabel").eq(thermostat.getDeviceLabel());
        sql.append("WHERE InventoryId").eq(inventoryId);

        yukonJdbcTemplate.update(sql);

        int accountId = getAccountIdForInventory(inventoryId);
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        accountEventLogService.thermostatLabelChanged(user, customerAccount.getAccountNumber(),
                                                      thermostat.getSerialNumber(),
                                                      oldThermostatLabel, thermostat.getDeviceLabel());
    }

    /**
     * Mapper class to map a resultset row into a LiteHardware
     */
    private class HardwareSummaryRowMapper implements YukonRowMapper<HardwareSummary> {
        @Override
        public HardwareSummary mapRow(YukonResultSet rs) throws SQLException {

            int inventoryId = rs.getInt("InventoryID");
            String deviceLabel = rs.getString("DeviceLabel");
            String manufacturerSerialNumber = rs.getString("ManufacturerSerialNumber");
            int hwDefinitionId = rs.getInt("hardwareDefinitionId");
            HardwareType hardwareType = HardwareType.valueOf(hwDefinitionId);
            
            HardwareSummary hardware = new HardwareSummary(new InventoryIdentifier(inventoryId, hardwareType),
                                                     deviceLabel,
                                                     manufacturerSerialNumber);

            return hardware;
        }
    }        
    
    /**
     * Mapper class to map a resultset row into a thermostat
     */
    private class ThermostatRowMapper implements YukonRowMapper<Thermostat> {

        YukonEnergyCompany yukonEnergyCompany;

        public ThermostatRowMapper(YukonEnergyCompany yukonEnergyCompany) {
            this.yukonEnergyCompany = yukonEnergyCompany;
        }

        public Thermostat mapRow(YukonResultSet rs) throws SQLException {

            Thermostat thermostat = new Thermostat();

            int id = rs.getInt("InventoryID");
            thermostat.setId(id);

            String serialNumber = rs.getString("ManufacturerSerialNumber");
            thermostat.setSerialNumber(serialNumber);

            String deviceLabel = rs.getString("DeviceLabel");
            thermostat.setDeviceLabel(deviceLabel);

            LiteStarsEnergyCompany liteStarsEnergyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            
            // Convert the category entryid into a InventoryCategory enum value
            int categoryEntryId = rs.getInt("CategoryId");
            int categoryDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                                                                                 YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY,
                                                                                 categoryEntryId);
            
            InventoryCategory category = InventoryCategory.valueOf(categoryDefinitionId);
            thermostat.setCategory(category);

            // Convert the hardware type entryid into a HardwareType enum value
            int typeEntryId = rs.getInt("LMHardwareTypeId");
            int typeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                             typeEntryId);
            HardwareType hardwareType = HardwareType.valueOf(typeDefinitionId);
            thermostat.setType(hardwareType);

            int routeId = rs.getInt("RouteId");
            if (routeId == CtiUtilities.NONE_ZERO_ID) {
                routeId = liteStarsEnergyCompany.getDefaultRouteId();
            }
            thermostat.setRouteId(routeId);

            // Convert the current state entryid into a HardwareStatus enum
            // value
            int statusEntryId = rs.getInt("CurrentStateId");
            HardwareStatus status = this.getStatus(statusEntryId, id, hardwareType);
            thermostat.setStatus(status);

            return thermostat;
        }

        /**
         * Helper method to determine the current status of a thermostat
         * @param statusEntryId - CurrentStateId in the InventoryBase table
         * @param thermostatId - Id of thermostat in question
         * @param type - Type of thermostat
         * @return - Status of thermostat
         */
        private HardwareStatus getStatus(int statusEntryId, int thermostatId, HardwareType type) {

            LiteStarsEnergyCompany liteStarsEnergyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());

            if (statusEntryId != 0) {
                int statusDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                                                                                   YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS,
                                                                                   statusEntryId);
                HardwareStatus status = HardwareStatus.valueOf(statusDefinitionId);
                return status;
            } else {

                // statusEntryId is 0, check the previous events for this device
                // to determine status

                boolean isSA = (type == HardwareType.SA_205) || (type == HardwareType.SA_305);

                List<LiteLMHardwareEvent> events = hardwareEventDao.getByInventoryId(thermostatId);
                for (LiteLMHardwareEvent event : events) {

                    int actionId = event.getActionID();

                    int actionDefinitionId = YukonListEntryHelper.getYukonDefinitionId(liteStarsEnergyCompany,
                                                                                       YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_ACTION,
                                                                                       actionId);

                    if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED == actionDefinitionId || isSA && YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG == actionDefinitionId) {
                        return HardwareStatus.AVAILABLE;
                    }
                    if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION == actionDefinitionId || YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION == actionDefinitionId) {
                        return HardwareStatus.TEMP_UNAVAILABLE;
                    }
                    if (YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION == actionDefinitionId) {
                        return HardwareStatus.UNAVAILABLE;
                    }
                }

                return HardwareStatus.AVAILABLE;

            }

        }

    }
    
    @Override
    public InventoryIdentifier getYukonInventory(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
        sql.append("FROM InventoryBase I");
        sql.append(  "LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
        sql.append(  "LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
        sql.append("WHERE I.InventoryId").eq(inventoryId);
        
        return yukonJdbcTemplate.queryForObject(sql, inventoryIdentifierMapper);
    }
    
    @Override
    public InventoryIdentifier getYukonInventoryForDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
        sql.append("FROM InventoryBase I");
        sql.append(  "LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
        sql.append(  "LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
        sql.append("WHERE I.DeviceId").eq(deviceId);
        
        return yukonJdbcTemplate.queryForObject(sql, inventoryIdentifierMapper);
    }
    
    @Override
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds) {
        
        Set<InventoryIdentifier> result = new HashSet<InventoryIdentifier>();
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT I.InventoryID, H.LMHardwareTypeID, M.MeterTypeID");
                sql.append("FROM InventoryBase I");
                sql.append(  "LEFT JOIN LMHardwareBase H on I.InventoryID = H.InventoryID");
                sql.append(  "LEFT JOIN MeterHardwareBase M on M.InventoryID = I.InventoryID");
                sql.append("WHERE I.InventoryId").in(subList);
                return sql;
            }
        };
        template.queryInto(generator, inventoryIds, inventoryIdentifierMapper, result);

        return result;
    }
    
    @Override
    public InventoryIdentifier getYukonInventory(String serialNumber, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT lmhb.inventoryId, yle.YukonDefinitionId");
        sql.append("FROM LMHardwareBase lmhb");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append(  "JOIN ECToInventoryMapping ec ON ec.InventoryId = lmhb.InventoryId");
        sql.append("WHERE lmhb.manufacturerSerialNumber").eq(serialNumber);
        sql.append(  "AND ec.EnergyCompanyId").eq(energyCompanyId);
        
        return yukonJdbcTemplate.queryForObject(sql, new LmHardwareInventoryIdentifierMapper());
    }
    
    @Override
    public Map<String, Integer> getSerialNumberToInventoryIdMap(Collection<String> serialNumbers, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMHB.ManufacturerSerialNumber, LMHB.InventoryId");
        sql.append("FROM LMHardwareBase LMHB");
        sql.append(  "JOIN ECToInventoryMapping ECTIM ON ECTIM.InventoryId = LMHB.InventoryId");
        sql.append("WHERE LMHB.ManufacturerSerialNumber").in(serialNumbers);
        sql.append(  "AND ECTIM.EnergyCompanyId").eq_k(energyCompanyId);
        
        final Map<String, Integer> serialNumberToInventoryMap = Maps.newHashMapWithExpectedSize(serialNumbers.size()); 
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
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
        sql.append(  "JOIN ECToInventoryMapping ECTIM ON ECTIM.InventoryId = LMHB.InventoryId");
        sql.append("WHERE LMHB.ManufacturerSerialNumber").in(serialNumbers);
        sql.append(  "AND ECTIM.EnergyCompanyId").eq_k(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
    }
    
    @Override
    public List<DisplayableLmHardware> getDisplayableLMHardware(List<? extends YukonInventory> yukonInventory) {
        DisplayableLmHardwareRowMapper mapper = new DisplayableLmHardwareRowMapper();
        
        Iterable<Integer> inventoryIds = InventoryUtils.convertYukonInventoryToIds(yukonInventory);
        
        SqlStatementBuilder sql = new SqlStatementBuilder(mapper.getBaseQuery().getSql());
        sql.append("WHERE lmhw.inventoryId").in(inventoryIds);
        sql.appendFragment(mapper.getOrderBy());
        
        return yukonJdbcTemplate.query(sql, mapper);
    }
    
    @Override
    public List<Integer> getInventoryIdsByAccount(int accountId){
        List<Integer> inventoryIds = new ArrayList<Integer>();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE AccountId").eq(accountId);
        inventoryIds = yukonJdbcTemplate.query(sql ,new IntegerRowMapper());
        
        return inventoryIds;
    }
    
    @Override
    public int getYukonDefinitionIdByEntryId(int entryId) {
    	String sql = "SELECT YukonDefinitionId FROM YukonListEntry WHERE entryId = ?";
    	
    	int defId = yukonJdbcTemplate.queryForInt(sql, entryId);
    	
    	return defId;
    }
    
    @Override
    public boolean checkAccountNumber(int inventoryId, String accountNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.AccountNumber");
        sql.append("FROM InventoryBase ib");
        sql.append(  "JOIN CustomerAccount ca ON ca.AccountId = ib.AccountId");
        sql.append("WHERE ib.InventoryId").eq(inventoryId);
        
        String result = yukonJdbcTemplate.queryForString(sql);
        
        return result.equalsIgnoreCase(accountNumber);
    }

    @Override
    public boolean checkdeviceType(int inventoryId, String deviceType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT yle.EntryText");
        sql.append("FROM LmHardwareBase lmhb");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryId = lmhb.LmHardwareTypeId");
        sql.append("WHERE lmhb.InventoryId").eq(inventoryId);
        
        String result = yukonJdbcTemplate.queryForString(sql);
        
        return result.equalsIgnoreCase(deviceType);
    }
    
    @Override
    public int getDeviceId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getAccountIdForInventory(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AccountId");
        sql.append("FROM InventoryBase");
        sql.append("WHERE InventoryId").eq(inventoryId);
        
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public HardwareType getHardwareTypeById(int hardwareTypeId) {
        Integer hardwareTypeDefinitionId;
        if (hardwareTypeId > 0) {
            /* This is a stars object that will live in either LMHardwareBase or MeterHardwareBase */
            YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId);
            hardwareTypeDefinitionId = entry.getYukonDefID();
        } else {
            /* This is a real MCT that exists as a yukon pao.  It will only have a row in InventoryBase 
             * so there is no type id. */
            hardwareTypeDefinitionId = 0;
        }
        
        return HardwareType.valueOf(hardwareTypeDefinitionId);
    }
    
    @Override
    public HardwareType getHardTypeByInventoryId(int inventoryId) {
        return getYukonInventory(inventoryId).getHardwareType();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SearchResult<InventorySearchResult> search(InventorySearch inventorySearch, 
                                                      Collection<Integer> ecIds, 
                                                      int start, 
                                                      int pageCount, 
                                                      final boolean starsMeters) {
        // Trim fields to null
        inventorySearch.trimFields();
        
        final boolean usePhone = StringUtils.isNotBlank(inventorySearch.getPhoneNumber());
        final boolean useWorkOrder = StringUtils.isNotBlank(inventorySearch.getWorkOrderNumber());
        
        SearchResult<InventorySearchResult> results = new SearchResult<InventorySearchResult>();
        
        SqlStatementBuilder selectCount = new SqlStatementBuilder();
        selectCount.append("SELECT COUNT(*)");
        
        SqlStatementBuilder selectData = new SqlStatementBuilder();
        selectData.append("SELECT IB.InventoryId,");
        selectData.append(  "IB.DeviceId,");
        selectData.append(  "LMHB.ManufacturerSerialNumber,");
        if (starsMeters) {
            selectData.append(  "MHB.MeterNumber,");
            selectData.append(  "MHB.MeterTypeID,");
        } else {
            selectData.append(  "DMG.MeterNumber,");
            selectData.append(  "YPO.Type,");
        }
        selectData.append(  "IM.EnergyCompanyId,");
        selectData.append(  "EC.Name EnergyCompanyName,");
        selectData.append(  "LMHB.LMHardwareTypeID,");
        selectData.append(  "IB.DeviceLabel,");
        selectData.append(  "CA.AccountId,");
        selectData.append(  "CA.AccountNumber,");
        selectData.append(  "CON.ContLastName,");
        if (usePhone) {
            selectData.append(  "CN.Notification,");
        }
        if (useWorkOrder) {
            selectData.append(  "WOB.OrderNumber,");
        }
        selectData.append(  "IB.AlternateTrackingNumber");

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM InventoryBase IB");
        sql.append(  "JOIN ECToInventoryMapping IM on IM.InventoryID = IB.InventoryID");
        sql.append(  "JOIN EnergyCompany EC on EC.EnergyCompanyID = IM.EnergyCompanyID");
        sql.append(  "LEFT JOIN LMHardwareBase LMHB on LMHB.InventoryID = IB.InventoryID");
        if (starsMeters) {
            sql.append(  "LEFT JOIN MeterHardwareBase MHB on MHB.InventoryID = IB.InventoryID");
        } else {
            sql.append(  "LEFT JOIN YukonPAObject YPO on YPO.PAObjectId = IB.DeviceID");
            sql.append(  "LEFT JOIN DeviceMeterGroup DMG on DMG.DeviceId = IB.DeviceID");
        }
        sql.append(  "JOIN CustomerAccount CA on CA.AccountID = IB.AccountID");
        sql.append(  "JOIN AccountSite ACS on ACS.AccountSiteID = CA.AccountSiteID");
        sql.append(  "JOIN Customer CUS on CA.CustomerID = CUS.CustomerID");
        sql.append(  "JOIN Contact CON on CON.ContactID = CUS.PrimaryContactID");
        if (usePhone) {
            sql.append(  "JOIN ContactNotification CN ON CN.ContactID = CON.ContactID AND CN.NotificationCategoryID = 5");
        }
        if (useWorkOrder) {
            sql.append(  "JOIN WorkOrderBase WOB on WOB.AccountID = CA.AccountID");
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
            whereClause.add(new SqlStatementBuilder("CN.Notification").startsWith(inventorySearch.getPhoneNumber()));//TODO FORMAT PN
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
        
        int total = yukonJdbcTemplate.queryForInt(selectCount.append(sql));
        
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
                if (usePhone) result.setPhoneNumber(rs.getString("Notification"));
                if (identifier.getHardwareType().isMeter()) {
                    result.setSerialNumber(rs.getString("MeterNumber"));
                    if (!starsMeters) {
                        result.setPaoType(rs.getEnum("Type", PaoType.class));
                    }
                } else {
                    result.setSerialNumber(rs.getString("ManufacturerSerialNumber"));
                }
                if (useWorkOrder) result.setWorkOrderNumber(rs.getString("OrderNumber"));
                
                return result;
            }
        };
        PagingExtractor<InventorySearchResult> extractor = new PagingExtractor<InventorySearchResult>(start, pageCount, mapper);
        List<InventorySearchResult> resultList = (List<InventorySearchResult>) yukonJdbcTemplate.query(selectData.append(sql), extractor);
        
        results.setResultList(resultList);
        results.setBounds(start, pageCount, total);
        
        return results;
    }
    
    @Override
    public int getCategoryIdForTypeId(int hardwareTypeId, YukonEnergyCompany ec) {
        
        HardwareType hardwareType = getHardwareTypeById(hardwareTypeId);

        /* The category id is the entry id of the yukon list entry for this category in this energy company. */
        int categoryDefId = hardwareType.getInventoryCategory().getDefinitionId();
        YukonListEntry categoryEntryOfEnergyCompany = starsDatabaseCache.getEnergyCompany(ec).getYukonListEntry(categoryDefId);
        return categoryEntryOfEnergyCompany.getEntryID();
    }

    @Override
    public List<String> getThermostatLabels(List<Integer> thermostatIds) {
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        List<String> deviceLabels = template.query(new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT IB.DeviceLabel");
                sql.append("FROM InventoryBase IB");
                sql.append("WHERE IB.inventoryId").in(subList);
                
                return sql;
            }
        }, thermostatIds, new StringRowMapper());
        
        return deviceLabels;
    }
    
    @Override
    public String getMeterNumberForDevice(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MeterNumber");
        sql.append("FROM DeviceMeterGroup");
        sql.append("WHERE Deviceid").eq(deviceId);
        return yukonJdbcTemplate.queryForString(sql);
    }
    
}