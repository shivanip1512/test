package com.cannontech.common.device.configuration.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Data access object for DeviceConfiguration. Handles all CRUD functionality
 */
public class DeviceConfigurationDao {

    public static final String TABLE_NAME = "DCConfiguration";
    public static final String CATEGORY_MAP_TABLE = "dcconfigurationcategory";
    public static final String DEVICE_TYPE_MAP_TABLE = "dcdeviceconfigurationtype";
    public static final String DEVICE_MAP_TABLE = "dcdeviceconfiguration";

    /**
     * Method to save a device configuration for a given device
     * @param paObjectId - The device to save the config for
     * @param configId The config to save
     */
    public static void save(int paObjectId, int configId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

        // Remove any old mappings
        String deleteSql = "DELETE FROM " + DEVICE_MAP_TABLE + " WHERE deviceid = ?";
        jdbcOps.update(deleteSql, new Object[] { paObjectId });

        // Save device / config mapping
        String mappingSql = "INSERT INTO " + DEVICE_MAP_TABLE
                + " (deviceid, configid) VALUES (?,?)";
        jdbcOps.update(mappingSql, new Object[] { paObjectId, configId });

    }

    /**
     * Method to get a map of key: device type, value: list of device
     * configurations
     * @return Map
     */
    public static Map<String, List<DeviceConfiguration>> getDeviceTypeConfigMap() {

        Map<String, List<DeviceConfiguration>> configMap = new HashMap<String, List<DeviceConfiguration>>();

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT devicetype, configtypeid FROM " + DEVICE_TYPE_MAP_TABLE;

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql, new SqlRowSetResultSetExtractor());

        while (rowSet.next()) {

            String deviceType = rowSet.getString(1);
            int configTypeId = rowSet.getInt(2);

            List<DeviceConfiguration> configList = getForType(configTypeId);

            configMap.put(deviceType, configList);

        }

        return configMap;
    }

    /**
     * Method to get a map of key: device type, value: device configuration type
     * @return Map
     */
    public static Map<String, Integer> getDeviceTypeConfigTypeMap() {

        Map<String, Integer> configMap = new HashMap<String, Integer>();

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT devicetype, configtypeid FROM " + DEVICE_TYPE_MAP_TABLE;

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql, new SqlRowSetResultSetExtractor());

        while (rowSet.next()) {

            String deviceType = rowSet.getString(1);
            int configTypeId = rowSet.getInt(2);

            configMap.put(deviceType, configTypeId);

        }

        return configMap;
    }

    /**
     * Method to save a device configuration
     * @param config The config to save
     */
    public static void save(DeviceConfiguration config) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String mainSql = null;
        String mappingSql = "INSERT INTO " + CATEGORY_MAP_TABLE
                + " (configid, categoryid) VALUES (?,?)";

        // Insert if id is null, otherwise update
        if (config.getId() == null) {
            mainSql = "INSERT INTO " + TABLE_NAME
                    + " (name, configtypeid, configid) VALUES (?,?,?)";
            config.setId(DaoFuncs.getNextId(TABLE_NAME));
        } else {
            mainSql = "UPDATE " + TABLE_NAME + " SET name = ?, configtypeid = ? WHERE configid = ?";

            // Remove any old mappings
            String deleteSql = "DELETE FROM " + CATEGORY_MAP_TABLE + " WHERE configid = ?";
            jdbcOps.update(deleteSql, new Object[] { config.getId() });
        }

        // Save config
        jdbcOps.update(mainSql,
                       new Object[] { config.getName(), config.getTypeId(), config.getId() });

        // Save config / category mappings
        Iterator categoryIter = config.getCategoryList().iterator();
        while (categoryIter.hasNext()) {
            Category category = (Category) categoryIter.next();

            // Save the category if it doesn't already exist
            if (category.getId() == null) {
                CategoryDao.save(category);
            }

            jdbcOps.update(mappingSql, new Object[] { config.getId(), category.getId() });

        }
    }

    /**
     * Method to delete a device configuration
     * @param config device configuration to delete
     */
    public static void delete(DeviceConfiguration config) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

        // Delete config / category mappings
        String sql = "DELETE FROM " + CATEGORY_MAP_TABLE + " WHERE configid = ?";
        jdbcOps.update(sql, new Object[] { config.getId() });

        // Delete config
        sql = "DELETE FROM " + TABLE_NAME + " WHERE configid = ?";
        jdbcOps.update(sql, new Object[] { config.getId() });

    }

    /**
     * Method to get the assigned configuration for a given device if one has
     * been assigned
     * @param deviceId - Id of device in question
     * @return Assigned config or null if none assigned
     */
    public static DeviceConfiguration getForDevice(int deviceId) {

        DeviceConfiguration config = null;

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT configid FROM " + DEVICE_MAP_TABLE + " WHERE deviceid = ?";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { deviceId },
                                                     new SqlRowSetResultSetExtractor());

        if (rowSet.next()) {

            int configId = rowSet.getInt(1);
            config = getForId(configId);

        }

        return config;

    }

    /**
     * Method to get a device configuration for a given id
     * @param id Id of device configuration
     * @return Fully loaded device configuration
     */
    public static DeviceConfiguration getForId(Integer id) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT configid, name, configtypeid FROM " + TABLE_NAME
                + " WHERE configid = ?";

        return (DeviceConfiguration) jdbcOps.queryForObject(sql,
                                                            new Object[] { id },
                                                            new DeviceConfigMapper());

    }

    /**
     * Method to get a list of device types for a given config type
     * @param configTypeId - Config type to get device types for
     * @return A list of device types
     */
    public static List<String> getDeviceTypesForConfigType(int configTypeId) {

        List<String> deviceTypeList = new ArrayList<String>();

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT devicetype FROM " + DEVICE_TYPE_MAP_TABLE + " WHERE configtypeid = ?";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { configTypeId },
                                                     new SqlRowSetResultSetExtractor());

        if (rowSet.next()) {

            String deviceType = rowSet.getString(1);
            deviceTypeList.add(deviceType);
        }

        return deviceTypeList;
    }

    /**
     * Method to get a list of devices that have been assigned a given config
     * @param configId - Id of config in question
     * @return A list of devices
     */
    public static List<Integer> getDeviceIdsConfig(int configId) {
        List<Integer> deviceIdList = new ArrayList<Integer>();

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT deviceid FROM " + DEVICE_MAP_TABLE + " WHERE configid = ?";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { configId },
                                                     new SqlRowSetResultSetExtractor());

        while (rowSet.next()) {

            Integer deviceId = rowSet.getInt(1);
            deviceIdList.add(deviceId);
        }

        return deviceIdList;
    }

    /**
     * Method to get a list of all device configurations of a given type
     * @param typeId Type of device configurations to get
     * @return A list of device configurations
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<DeviceConfiguration> getForType(int typeId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT configid, name, configtypeid FROM " + TABLE_NAME
                + " WHERE configtypeid = ?";

        // Load type once
        final Type type = TypeDao.getConfigTypeForId(typeId);

        // Load, populate and return config list
        return jdbcOps.query(sql, new Object[] { typeId }, new DeviceConfigMapper() {
            public Type getType(Integer typeId) {
                return type;
            }
        });

    }

    /**
     * Method to assign a config to each device in a list
     * @param configId - Id of config to assign
     * @param devices - List of devices to assign config to
     */
    public static void assignConfigToDevices(int configId, List<LiteYukonPAObject> devices) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "INSERT INTO " + DEVICE_MAP_TABLE + " VALUES (?,?)";

        Iterator<LiteYukonPAObject> deviceIter = devices.iterator();
        while (deviceIter.hasNext()) {

            LiteYukonPAObject device = deviceIter.next();

            jdbcOps.update(sql, new Object[] { device.getLiteID(), configId });
        }

    }

    /**
     * Method to remove config assignments for each device in a list if a config
     * is assigned
     * @param devices - A list of devices to remove config assignments for
     */
    public static void removeConfigAssignmentForDevices(List<LiteYukonPAObject> devices) {

        if (devices.size() > 0) {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

            StringBuffer ids = new StringBuffer();
            Iterator<LiteYukonPAObject> deviceIter = devices.iterator();
            while (deviceIter.hasNext()) {

                LiteYukonPAObject device = deviceIter.next();

                ids.append(device.getLiteID() + ((deviceIter.hasNext()) ? "," : ""));

            }

            String sql = "DELETE FROM " + DEVICE_MAP_TABLE + " WHERE deviceid IN (" + ids + ")";
            jdbcOps.update(sql);
        }
    }

    /**
     * Method to remove config assignments for each device assigned for a given
     * config
     * @param configId - Id of config to remove all devices assignments from
     */
    public static void removeConfigAssignmentForConfig(int configId) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

        String sql = "DELETE FROM " + DEVICE_MAP_TABLE + " WHERE configid IN (" + configId + ")";
        jdbcOps.update(sql);

    }

    /**
     * Method to determine whether a configuration is assigned to any devices
     * @param configId - Id of config in question
     * @return True if the config is assigned to any device
     */
    public static boolean isConfigInUse(int configId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT deviceid, configid FROM " + DEVICE_MAP_TABLE + " WHERE configid = ?";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { configId },
                                                     new SqlRowSetResultSetExtractor());

        return rowSet.next();

    }

    /**
     * Mapping class to process a result set row into a device configuration
     */
    private static class DeviceConfigMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            DeviceConfiguration config = new DeviceConfiguration();
            config.setId(rs.getInt(1));
            config.setName(rs.getString(2));

            config.setType(this.getType((Integer) rs.getInt(3)));
            config.setCategoryList(this.getCategoryList(config.getId()));

            return config;
        }

        public Type getType(Integer typeId) throws SQLException {
            Type type = TypeDao.getConfigTypeForId(typeId);

            return type;
        }

        public List<Category> getCategoryList(Integer configId) throws SQLException {
            return CategoryDao.getForConfig(configId);
        }
    }
}
