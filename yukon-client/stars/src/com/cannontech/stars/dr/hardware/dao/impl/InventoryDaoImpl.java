package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.DisplayableLmHardwareRowMapper;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareStatus;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.util.InventoryUtils;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

/**
 * Implementation class for InventoryDao
 */
public class InventoryDaoImpl implements InventoryDao {

    private ECMappingDao ecMappingDao;
    private EnergyCompanyService energyCompanyService;
    private LMHardwareEventDao hardwareEventDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;

    private HardwareSummaryRowMapper hardwareSummaryRowMapper = new HardwareSummaryRowMapper();

    // Static list of thermostat device types
    private static List<Integer> THERMOSTAT_TYPES = new ArrayList<Integer>();
    private static final String selectHardwareSummarySql;
    static {
        
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO);

        SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
        sql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        sql.append(    "lmhb.manufacturerSerialNumber,");
        sql.append(    "le.yukonDefinitionId AS hardwareDefinitionId");
        sql.append("FROM inventoryBase ib");
        sql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId ");
        sql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId ");
        selectHardwareSummarySql = sql.toString();
    }

    @Override
    public List<Thermostat> getThermostatsByAccount(CustomerAccount account) {
    	
    	int accountId = account.getAccountId();
    	return getThermostatsByAccountId(accountId);
    }
    
    @Override
    public List<Thermostat> getThermostatsByAccountId(int accountId) {
        String thermostatTypes = SqlStatementBuilder.convertToSqlLikeList(THERMOSTAT_TYPES);        
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);
        StringBuilder sql = new StringBuilder("SELECT ib.*, lmhb.* ");
        sql.append(" FROM InventoryBase ib, LMHardwareBase lmhb ");
        sql.append(" WHERE ib.accountid = ? ");
        sql.append(" AND lmhb.inventoryid = ib.inventoryid ");
        sql.append(" AND lmhb.LMHardwareTypeID IN ");
        sql.append(" (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID IN (" + thermostatTypes + "))");
        List<Thermostat> thermostatList = yukonJdbcTemplate.query(sql.toString(),
                                                             new ThermostatRowMapper(energyCompany),
                                                             accountId);
        return thermostatList;
    }

    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectHardwareSummarySql);
        sql.append("WHERE ib.accountID = ").appendArgument(accountId);

        List<HardwareSummary> hardwareList = yukonJdbcTemplate.query(sql.getSql(),
                                                                   hardwareSummaryRowMapper,
                                                                   sql.getArguments());

        return hardwareList;
    }
    
    @Override
    public HardwareSummary findHardwareSummaryById(int inventoryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectHardwareSummarySql);
        sql.append("WHERE ib.InventoryID = ").appendArgument(inventoryId);

        HardwareSummary hardware = null;
        try {
            hardware = yukonJdbcTemplate.queryForObject(sql.getSql(),
                                                   hardwareSummaryRowMapper,
                                                   sql.getArguments());
        } catch (EmptyResultDataAccessException e) {}

        return hardware;
    }    

    @Override
    public Map<Integer, HardwareSummary> findHardwareSummariesById(
            Iterable<Integer> inventoryIds) {
        ChunkingMappedSqlTemplate template =
            new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectHardwareSummarySql);
                sql.append("WHERE ib.inventoryId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        ParameterizedRowMapper<Map.Entry<Integer, HardwareSummary>> rowMapper =
            new ParameterizedRowMapper<Entry<Integer,HardwareSummary>>() {
            @Override
            public Entry<Integer, HardwareSummary> mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                HardwareSummary hardware = hardwareSummaryRowMapper.mapRow(rs, rowNum);
                Integer inventoryId = hardware.getInventoryId();
                return Maps.immutableEntry(inventoryId, hardware);
            }
        };

        Map<Integer, HardwareSummary> retVal =
            template.mappedQuery(sqlGenerator, inventoryIds, rowMapper, typeMapper);
        return retVal;
    }

    @Override
    public List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectHardwareSummarySql);
        sql.append("WHERE ib.accountID = ").appendArgument(account.getAccountId());
        sql.append(" AND lmhb.LMHardwareTypeID IN ");
        sql.append(" (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID IN (", THERMOSTAT_TYPES, "))");

        List<HardwareSummary> thermostatList = yukonJdbcTemplate.query(sql.getSql(),
                                                                   hardwareSummaryRowMapper,
                                                                   sql.getArguments());        
        
        return thermostatList;
    }
    
    @Override
    public Thermostat getThermostatById(int thermostatId) {

        YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(thermostatId);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM InventoryBase IB");
        sql.append("JOIN LMHardwareBase LMHB ON LMHB.inventoryId = IB.inventoryId ");
        sql.append("WHERE IB.inventoryId").eq(thermostatId);

        Thermostat thermostat = yukonJdbcTemplate.queryForObject(sql, new ThermostatRowMapper(yukonEnergyCompany));
        return thermostat;
    }

    @Override
    @Transactional
    public void save(Thermostat thermostat) {

        StringBuilder sql = new StringBuilder("UPDATE InventoryBase");
        sql.append(" SET DeviceLabel = ?");
        sql.append(" WHERE InventoryId = ?");

        int id = thermostat.getId();
        String label = thermostat.getDeviceLabel();
        yukonJdbcTemplate.update(sql.toString(), label, id);
    }

    /**
     * Mapper class to map a resultset row into a LiteHardware
     */
    private class HardwareSummaryRowMapper implements
            ParameterizedRowMapper<HardwareSummary> {
        @Override
        public HardwareSummary mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int inventoryId = rs.getInt("InventoryID");
            String deviceLabel = rs.getString("DeviceLabel");
            String manufacturerSerialNumber = rs.getString("ManufacturerSerialNumber");
            int hwDefinitionId = rs.getInt("hardwareDefinitionId");
            HardwareType hardwareType = HardwareType.valueOf(hwDefinitionId);

            HardwareSummary hardware = new HardwareSummary(inventoryId,
                                                     deviceLabel,
                                                     manufacturerSerialNumber,
                                                     hardwareType);

            return hardware;
        }
    }        
    
    /**
     * Mapper class to map a resultset row into a thermostat
     */
    private class ThermostatRowMapper implements ParameterizedRowMapper<Thermostat> {

        YukonEnergyCompany yukonEnergyCompany;

        public ThermostatRowMapper(YukonEnergyCompany yukonEnergyCompany) {
            this.yukonEnergyCompany = yukonEnergyCompany;
        }

        @Override
        public Thermostat mapRow(ResultSet rs, int rowNum) throws SQLException {

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
            YukonListEntry categoryListEntry = 
                liteStarsEnergyCompany.getYukonListEntry(categoryEntryId);
            int categoryDefinitionId = categoryListEntry.getYukonDefID();
            
            InventoryCategory category = InventoryCategory.valueOf(categoryDefinitionId);
            thermostat.setCategory(category);

            // Convert the hardware type entryid into a HardwareType enum value
            int typeEntryId = rs.getInt("LMHardwareTypeId");
            YukonListEntry hardwareTypeListEntry = 
                liteStarsEnergyCompany.getYukonListEntry(typeEntryId);
            int hardwareTypeDefinitionId = hardwareTypeListEntry.getYukonDefID();

            HardwareType hardwareType = HardwareType.valueOf(hardwareTypeDefinitionId);
            thermostat.setType(hardwareType);

            int routeId = rs.getInt("RouteId");
            if (routeId == 0) {
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
                YukonListEntry statusListEntry = 
                    liteStarsEnergyCompany.getYukonListEntry(statusEntryId);
                int statusDefinitionId = statusListEntry.getYukonDefID();
                HardwareStatus status = HardwareStatus.valueOf(statusDefinitionId);
                return status;
            } else {

                // statusEntryId is 0, check the previous events for this device
                // to determine status

                boolean isSA = (type == HardwareType.SA_205) || (type == HardwareType.SA_305);

                List<LiteLMHardwareEvent> events = hardwareEventDao.getByInventoryId(thermostatId);
                for (LiteLMHardwareEvent event : events) {

                    int actionId = event.getActionID();

                    YukonListEntry actionListEntry = 
                        liteStarsEnergyCompany.getYukonListEntry(actionId);
                    int actionDefinitionId = actionListEntry.getYukonDefID();

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
        sql.append("SELECT yle.YukonDefinitionId");
        sql.append("FROM LMHardwareBase lmhb");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append("WHERE lmhb.InventoryId").eq(inventoryId);
        
        int hardwareTypeDefinitionId = yukonJdbcTemplate.queryForInt(sql);
        
        return new InventoryIdentifier(inventoryId, HardwareType.valueOf(hardwareTypeDefinitionId));
    }
    
    public static class InventoryIdentifierMapper implements YukonRowMapper<InventoryIdentifier> {
        @Override
        public InventoryIdentifier mapRow(YukonResultSet rs) throws SQLException {
            int inventoryId = rs.getInt("InventoryId");
            HardwareType type = rs.getEnum("YukonDefinitionId", HardwareType.class);
            
            return new InventoryIdentifier(inventoryId, type);
        }
    }
    
    @Override
    public Set<InventoryIdentifier> getYukonInventory(Collection<Integer> inventoryIds) {
        
        Set<InventoryIdentifier> result = new HashSet<InventoryIdentifier>();
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT lmhb.inventoryId, yle.YukonDefinitionId");
                sql.append("FROM LMHardwareBase lmhb");
                sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
                sql.append("WHERE lmhb.InventoryId").in(subList);
                return sql;
            }
        };
        template.queryInto(generator, inventoryIds, new InventoryIdentifierMapper(), result);

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
        
        return yukonJdbcTemplate.queryForObject(sql, new InventoryIdentifierMapper());
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
        
        String sql = "SELECT InventoryId FROM InventoryBase where AccountId = ?";
        inventoryIds = yukonJdbcTemplate.query(sql,new IntegerRowMapper(), accountId);
        
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

    // DI Setters
    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate jdbcTemplate) {
        this.yukonJdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
        this.hardwareEventDao = hardwareEventDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}