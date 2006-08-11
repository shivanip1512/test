package com.cannontech.database.cache.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.device.configuration.Type;

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
     * @throws SQLException
     */
    public static Type getConfigTypeForId(Integer id) throws SQLException {

        return getTypeForId(CONFIG_TABLE, CONFIG_COLUMN, id);

    }

    /**
     * Method to get a category type for a given id
     * @param id Id of category type
     * @return category type
     * @throws SQLException
     */
    public static Type getCategoryTypeForId(Integer id) throws SQLException {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT categorytypeid, name, categorygroup, level, description FROM "
                + CATEGORY_TABLE + " WHERE categorytypeid = ?";

        return (Type) jdbcOps.queryForObject(sql, new Object[] { id }, new CategoryTypeMapper());
    }

    /**
     * Method to get a list of compatible config types for a given device type
     * @param deviceType - Device type to get config types for
     * @return A list of config types
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getConfigTypesForDevice(String deviceType) throws SQLException {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT c." + CONFIG_COLUMN + ", c.name, c.description FROM " + CONFIG_TABLE
                + " c, " + DEVICE_CONFIG_TABLE + " dc WHERE dc.devicetype = ? AND c."
                + CONFIG_COLUMN + " = dc." + CONFIG_COLUMN;

        return jdbcOps.query(sql, new Object[] { deviceType }, new TypeMapper());

    }

    /**
     * Method to retrieve all config types
     * @return List of all config types
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getAllConfigTypes() throws SQLException {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT " + CONFIG_COLUMN + ", name, description FROM " + CONFIG_TABLE;

        return jdbcOps.query(sql, new TypeMapper());
    }

    /**
     * Method to retrieve all category types for a given config type id
     * @param configTypeId Id of config type to get all category types for
     * @return a list of all category types that are valid for the given config
     *         type
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<Type> getAllCategoryTypesForConfigType(Integer configTypeId)
            throws SQLException {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT t." + CATEGORY_COLUMN + ", t.name, t.categorygroup, t.level, "
                + "t.description FROM " + CATEGORY_TABLE + " t, " + CONFIG_CATEGORY_TABLE
                + " mt WHERE mt." + CONFIG_COLUMN + " = ? AND t." + CATEGORY_COLUMN + " = mt."
                + CATEGORY_COLUMN;

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
        String sql = "SELECT " + column + ", name, description FROM " + table + " WHERE " + column
                + " = ?";

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
            type.setDescription(rs.getString(3));

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
            type.setGroup(rs.getString(3));
            type.setLevel(rs.getString(4));

            return type;
        }
    }
}
