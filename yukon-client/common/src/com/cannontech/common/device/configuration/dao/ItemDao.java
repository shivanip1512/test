package com.cannontech.common.device.configuration.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Data access object for Item. Handles all CRUD functionality
 */
public class ItemDao {

    private static final String TABLE_NAME = "DCCategoryItem";
    private static final String TYPE_TABLE_NAME = "DCCategoryItemType";
    private static final String MAP_TABLE_NAME = "DCItemType";
    private static final String VALUE_TABLE_NAME = "DCItemValue";

    /**
     * Method to get a list of all items for a given category
     * @param categoryId Id of category to get items for
     * @return A list of items
     */
    @SuppressWarnings("unchecked")
    public static List<Item> getForCategory(int categoryId) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT ci.itemtypeid, ci.value, i.name, i.displayname, i.validationtype, "
                + "i.required, i.minvalue, i.maxvalue, i.defaultvalue,i.description FROM "
                + TABLE_NAME + " ci, " + MAP_TABLE_NAME + " i WHERE ci.categoryid = ? AND "
                + "ci.itemtypeid = i.itemtypeid ORDER BY i.name";

        return jdbcOps.query(sql, new Object[] { categoryId }, new ItemMapper());
    }

    /**
     * Method to delete all items for a given category
     * @param categoryId - Category to delete items for
     */
    public static void deleteForCategory(Integer categoryId) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE categoryid = ?";

        jdbcOps.update(sql, new Object[] { categoryId });

    }

    /**
     * Method to retrieve all default items for a given category type id
     * @param categoryTypeId Id of category type to get all item types for
     * @return a list of default items
     */
    @SuppressWarnings("unchecked")
    public static List<Item> getDefaultItemsForCategoryType(Integer categoryTypeId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT i.itemtypeid, i.name, i.displayname, i.validationtype, i.required, "
                + "i.minvalue, i.maxvalue, i.defaultvalue, i.description FROM " + MAP_TABLE_NAME
                + " i, " + TYPE_TABLE_NAME + " t WHERE t.categorytypeid = ? AND "
                + "t.itemtypeid = i.itemtypeid ORDER BY i.name";

        return jdbcOps.query(sql, new Object[] { categoryTypeId }, new DefaultItemMapper());

    }

    /**
     * Helper method to add all of the item's possible values to an item.
     * @param item
     */
    @SuppressWarnings("unchecked")
    private static void loadPossibleItemValues(Item item) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT value FROM " + VALUE_TABLE_NAME
                + " WHERE itemtypeid = ? ORDER BY valueorder";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { item.getId() },
                                                     new SqlRowSetResultSetExtractor());

        while (rowSet.next()) {
            item.addPossibleValue(rowSet.getString(1));
        }
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
            item.setDisplayName(rs.getString(4));
            item.setValueType(rs.getString(5));
            String requiredString = rs.getString(6);
            item.setRequired((requiredString.equalsIgnoreCase("Y")) ? true : false);
            item.setMinValue(rs.getInt(7));
            item.setMaxValue(rs.getInt(8));
            item.setDefaultValue(rs.getString(9));
            item.setDescription(rs.getString(10));

            ItemDao.loadPossibleItemValues(item);
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
            item.setDisplayName(rs.getString(3));
            item.setValueType(rs.getString(4));
            String requiredString = rs.getString(5);
            item.setRequired((requiredString.equalsIgnoreCase("Y")) ? true : false);
            item.setMinValue(rs.getInt(6));
            item.setMaxValue(rs.getInt(7));
            item.setDefaultValue(rs.getString(8));
            item.setDescription(rs.getString(9));

            ItemDao.loadPossibleItemValues(item);

            return item;
        }
    }
}
