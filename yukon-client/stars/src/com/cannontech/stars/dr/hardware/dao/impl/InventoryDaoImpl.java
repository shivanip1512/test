package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareStatus;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.InventoryCategory;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.util.YukonListEntryHelper;

/**
 * Implementation class for InventoryDao
 */
public class InventoryDaoImpl implements InventoryDao {

    // Static list of thermostat device types
    private static List<Integer> THERMOSTAT_TYPES = new ArrayList<Integer>();
    private static final SqlStringStatementBuilder selectHardwareSummarySql
        = new SqlStringStatementBuilder();
    static {
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP);
        THERMOSTAT_TYPES.add(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_UTILITYPRO);

        selectHardwareSummarySql.append("SELECT ib.inventoryId, ib.deviceLabel,");
        selectHardwareSummarySql.append(    "lmhb.manufacturerSerialNumber,");
        selectHardwareSummarySql.append(    "le.yukonDefinitionId AS hardwareDefinitionId");
        selectHardwareSummarySql.append("FROM inventoryBase ib");
        selectHardwareSummarySql.append(    "JOIN lmHardwareBase lmhb ON ib.inventoryId = lmhb.inventoryId ");        
        selectHardwareSummarySql.append(    "JOIN yukonListEntry le ON lmhb.lmHardwareTypeId = le.entryId ");        
    }

    private SimpleJdbcTemplate jdbcTemplate;
    private ECMappingDao ecMappingDao;
    private LMHardwareEventDao hardwareEventDao;
    private HardwareSummaryRowMapper hardwareSummaryRowMapper = new HardwareSummaryRowMapper();

    @Autowired
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setHardwareEventDao(LMHardwareEventDao hardwareEventDao) {
        this.hardwareEventDao = hardwareEventDao;
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
        List<Thermostat> thermostatList = jdbcTemplate.query(sql.toString(),
                                                             new ThermostatRowMapper(energyCompany),
                                                             accountId);
        return thermostatList;
    }

    @Override
    public List<HardwareSummary> getAllHardwareSummaryForAccount(int accountId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectHardwareSummarySql);
        sql.append("WHERE ib.accountID = ").appendArgument(accountId);

        List<HardwareSummary> hardwareList = jdbcTemplate.query(sql.getSql(),
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
            hardware = jdbcTemplate.queryForObject(sql.getSql(),
                                                   hardwareSummaryRowMapper,
                                                   sql.getArguments());
        } catch (EmptyResultDataAccessException e) {}

        return hardware;
    }    

    @Override
    public List<HardwareSummary> getThermostatSummaryByAccount(CustomerAccount account) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectHardwareSummarySql);
        sql.append("WHERE ib.accountID = ").appendArgument(account.getAccountId());
        sql.append(" AND lmhb.LMHardwareTypeID IN ");
        sql.append(" (SELECT entryid FROM YukonListEntry WHERE YukonDefinitionID IN (", THERMOSTAT_TYPES, "))");

        List<HardwareSummary> thermostatList = jdbcTemplate.query(sql.getSql(),
                                                                   hardwareSummaryRowMapper,
                                                                   sql.getArguments());        
        
        return thermostatList;
    }
    
    @Override
    public Thermostat getThermostatById(int thermostatId) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(thermostatId);

        StringBuilder sql = new StringBuilder("SELECT ib.*, lmhb.* ");
        sql.append(" FROM InventoryBase ib, LMHardwareBase lmhb ");
        sql.append(" WHERE  lmhb.inventoryid = ib.inventoryid ");
        sql.append(" AND ib.inventoryid = ?");

        Thermostat thermostat = jdbcTemplate.queryForObject(sql.toString(),
                                                            new ThermostatRowMapper(energyCompany),
                                                            thermostatId);
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
        jdbcTemplate.update(sql.toString(), label, id);
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
    private class ThermostatRowMapper implements
            ParameterizedRowMapper<Thermostat> {

        LiteStarsEnergyCompany energyCompany;

        public ThermostatRowMapper(LiteStarsEnergyCompany energyCompany) {
            this.energyCompany = energyCompany;
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

            // Convert the category entryid into a InventoryCategory enum value
            int categoryEntryId = rs.getInt("CategoryId");
            int categoryDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                                 YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY,
                                                                                 categoryEntryId);
            InventoryCategory category = InventoryCategory.valueOf(categoryDefinitionId);
            thermostat.setCategory(category);

            // Convert the hardware type entryid into a HardwareType enum value
            int typeEntryId = rs.getInt("LMHardwareTypeId");
            int typeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                             typeEntryId);
            HardwareType type = HardwareType.valueOf(typeDefinitionId);
            thermostat.setType(type);

            int routeId = rs.getInt("RouteId");
            if (routeId == 0) {
                routeId = energyCompany.getDefaultRouteID();
            }
            thermostat.setRouteId(routeId);

            // Convert the current state entryid into a HardwareStatus enum
            // value
            int statusEntryId = rs.getInt("CurrentStateId");
            HardwareStatus status = this.getStatus(statusEntryId, id, type);
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
        private HardwareStatus getStatus(int statusEntryId, int thermostatId,
                HardwareType type) {

            if (statusEntryId != 0) {
                int statusDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
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
                    int actionDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
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
    public List<Integer> getInventoryIdsByAccount(int accountId){
        List<Integer> inventoryIds = new ArrayList<Integer>();
        
        String sql = "SELECT InventoryId FROM InventoryBase where AccountId = ?";
        inventoryIds = jdbcTemplate.query(sql,new IntegerRowMapper(), accountId);
        
        return inventoryIds;
    }
    
    @Override
    public int getYukonDefinitionIdByEntryId(int entryId) {
    	String sql = "SELECT YukonDefinitionId FROM YukonListEntry WHERE entryId = ?";
    	
    	int defId = jdbcTemplate.queryForInt(sql, entryId);
    	
    	return defId;
    }

}
