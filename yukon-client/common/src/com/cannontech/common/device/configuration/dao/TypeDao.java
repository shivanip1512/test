package com.cannontech.common.device.configuration.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Data access object for Type. Handles all CRUD functionality
 */
public class TypeDao {

    private static String DEVICE_CONFIG_TABLE = "DCDeviceConfigurationType";
    private static String CONFIG_TABLE = "DCConfigurationType";
    private static String CONFIG_COLUMN = "ConfigTypeID";
    private static String CONFIG_CATEGORY_TABLE = "DCConfigurationCategoryType";
    private static String CATEGORY_TABLE = "DCCategoryType";
    private static String CATEGORY_COLUMN = "CategoryTypeID";

    /**
     * Method to get a config type for a given id
     * @param id Id of config type
     * @return config type
     */
    public static Type getConfigTypeForId(Integer id) {

        return getTypeForId(CONFIG_TABLE, CONFIG_COLUMN, id);

    }

    /**
     * Method to get a category type for a given id
     * @param id Id of category type
     * @return category type
     */
    public static Type getCategoryTypeForId(Integer id) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT categorytypeid, name, displayname, categorygroup, categorytypelevel, "
                + "description FROM " + CATEGORY_TABLE + " WHERE categorytypeid = ?";

        return (Type) jdbcOps.queryForObject(sql, new Object[] { id }, new CategoryTypeMapper());
    }

    /**
     * Method to get a list of compatible config types for a given device type
     * @param deviceType - Device type to get config types for
     * @return A list of config types
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getConfigTypesForDevice(String deviceType) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT c." + CONFIG_COLUMN + ", c.name, c.displayname, c.description FROM "
                + CONFIG_TABLE + " c, " + DEVICE_CONFIG_TABLE
                + " dc WHERE dc.devicetype = ? AND c." + CONFIG_COLUMN + " = dc." + CONFIG_COLUMN;

        return jdbcOps.query(sql, new Object[] { deviceType }, new TypeMapper());

    }

    /**
     * Method to retrieve all config types
     * @return List of all config types
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getAllConfigTypes() {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT " + CONFIG_COLUMN + ", name, displayname, description FROM "
                + CONFIG_TABLE;

        return jdbcOps.query(sql, new TypeMapper());
    }

    /**
     * Method to retrieve all category types for a given config type id
     * @param configTypeId Id of config type to get all category types for
     * @return a list of all category types that are valid for the given config
     *         type
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getAllCategoryTypesForConfigType(Integer configTypeId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT t." + CATEGORY_COLUMN + ", t.name, t.displayname, t.categorygroup, "
                + "t.categorytypelevel, " + "t.description FROM " + CATEGORY_TABLE + " t, "
                + CONFIG_CATEGORY_TABLE + " mt WHERE mt." + CONFIG_COLUMN + " = ? AND t."
                + CATEGORY_COLUMN + " = mt." + CATEGORY_COLUMN;

        return jdbcOps.query(sql, new Object[] { configTypeId }, new CategoryTypeMapper());
    }

    /**
     * Helper method to load a type
     * @param table - Table to get type info from
     * @param column - Column name for the type id
     * @param id - Id of type to load
     * @return Loaded type
     */
    private static Type getTypeForId(String table, String column, Integer id) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT " + column + ", name, displayname, description FROM " + table
                + " WHERE " + column + " = ?";

        return (Type) jdbcOps.queryForObject(sql, new Object[] { id }, new TypeMapper());
    }

    /**
     * Mapping class to process a result set row into a type
     */
    private static class TypeMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            Type type = new Type();
            type.setId(rs.getInt(1));
            type.setName(rs.getString(2));
            type.setDisplayName(rs.getString(3));
            type.setDescription(rs.getString(4));

            return type;
        }
    }

    /**
     * Mapping class to process a result set row into a category type
     */
    private static class CategoryTypeMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            Type type = new Type();
            type.setId(rs.getInt(1));
            type.setName(rs.getString(2));
            type.setDisplayName(rs.getString(3));
            type.setGroup(rs.getString(4));
            type.setLevel(rs.getString(5));
            type.setDescription(rs.getString(6));

            return type;
        }
    }
}
