package com.cannontech.database.cache.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.device.configuration.Item;

/**
 * Data access object for Item. Handles all CRUD functionality
 */
public class ItemDao {

    private static final String TABLE_NAME = "DCCategoryItem";
    private static final String TYPE_TABLE_NAME = "DCCategoryItemType";
    private static final String MAP_TABLE_NAME = "DCItemType";

    /**
     * Method to get a list of all items for a given category
     * @param categoryId Id of category to get items for
     * @return A list of items
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<Item> getForCategory(int categoryId) throws SQLException {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT ci.itemtypeid, ci.value, i.name, i.validationtype, i.required, "
                + "i.minvalue, i.maxvalue, i.description FROM " + TABLE_NAME + " ci, "
                + MAP_TABLE_NAME + " i WHERE ci.categoryid = ? AND ci.itemtypeid = i.itemtypeid";

        return jdbcOps.query(sql, new Object[] { categoryId }, new ItemMapper());
    }

    /**
     * Method to delete all items for a given category
     * @param categoryId - Category to delete items for
     * @throws SQLException
     */
    public static void deleteForCategory(Integer categoryId) throws SQLException {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE categoryid = ?";

        jdbcOps.update(sql, new Object[] { categoryId });

    }

    /**
     * Method to retrieve all default items for a given category type id
     * @param categoryTypeId Id of category type to get all item types for
     * @return a list of default items
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static List<Item> getDefaultItemsForCategoryType(Integer categoryTypeId)
            throws SQLException {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT i.itemtypeid, i.name, i.validationtype, i.required, i.minvalue, "
                + "i.maxvalue, i.description FROM " + MAP_TABLE_NAME + " i, " + TYPE_TABLE_NAME
                + " t WHERE t.categorytypeid = ? AND " + "t.itemtypeid = i.itemtypeid";

        return jdbcOps.query(sql, new Object[] { categoryTypeId }, new DefaultItemMapper());

    }

    /**
     * Mapping class to process a result set row into an item
     */
    private static class ItemMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            Item item = new Item();
            item.setId(rs.getInt(1));
            item.setValue(rs.getString(2));
            item.setName(rs.getString(3));
            item.setValueType(rs.getString(4));
            String requiredString = rs.getString(5);
            item.setRequired((requiredString.equalsIgnoreCase("Y")) ? true : false);
            item.setMinValue(rs.getInt(6));
            item.setMaxValue(rs.getInt(7));
            item.setDescription(rs.getString(8));

            return item;
        }
    }

    /**
     * Mapping class to process a result set row into an item with no value
     */
    private static class DefaultItemMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            Item item = new Item();
            item.setId(rs.getInt(1));
            item.setName(rs.getString(2));
            item.setValueType(rs.getString(3));
            String requiredString = rs.getString(4);
            item.setRequired((requiredString.equalsIgnoreCase("Y")) ? true : false);
            item.setMinValue(rs.getInt(5));
            item.setMaxValue(rs.getInt(6));
            item.setDescription(rs.getString(7));

            return item;
        }
    }
}
