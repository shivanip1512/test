package com.cannontech.common.device.configuration.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Data access object for Category. Handles all CRUD functionality
 */
public class CategoryDao {

    public static final String TABLE_NAME = "DCCategory";
    public static final String ITEM_MAP_TABLE = "DCCategoryItem";

    /**
     * Method to save a category
     * @param category The category to save
     */
    public static void save(Category category) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String mainSql = null;
        String mappingSql = "INSERT INTO " + ITEM_MAP_TABLE
                + " (categoryid, itemtypeid, value) VALUES (?,?,?)";

        // Insert if id is null, otherwise update
        if (category.getId() == null) {
            mainSql = "INSERT INTO " + TABLE_NAME
                    + " (name, categorytypeid, categoryid) VALUES (?,?,?)";
            category.setId(DaoFuncs.getNextId(TABLE_NAME));
        } else {
            mainSql = "UPDATE " + TABLE_NAME
                    + " SET name = ?, categorytypeid = ? WHERE categoryid = ?";

            // Remove any old mappings
            ItemDao.deleteForCategory(category.getId());
        }

        // Save category
        jdbcOps.update(mainSql, new Object[] { category.getName(), category.getTypeId(),
                category.getId() });

        // Save items and category / item mappings
        Iterator itemIter = category.getItemList().iterator();
        while (itemIter.hasNext()) {
            Item item = (Item) itemIter.next();

            jdbcOps.update(mappingSql, new Object[] { category.getId(), item.getId(),
                    item.getValue() });

        }
    }

    /**
     * Method to delete a category
     * @param category category to delete
     */
    public static void delete(Category category) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

        // Delete category / item mappings
        String sql = "DELETE FROM " + ITEM_MAP_TABLE + " WHERE categoryid = ?";
        jdbcOps.update(sql, new Object[] { category.getId() });

        // Delete items
        ItemDao.deleteForCategory(category.getId());

        // Delete category
        sql = "DELETE FROM " + TABLE_NAME + " WHERE categoryid = ?";
        jdbcOps.update(sql, new Object[] { category.getId() });

    }

    /**
     * Method to get a category for a given id
     * @param id Id of category
     * @return Fully loaded category
     */
    public static Category getForId(Integer id) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT categoryid, name, categorytypeid FROM " + TABLE_NAME
                + " WHERE categoryid = ?";

        return (Category) jdbcOps.queryForObject(sql, new Object[] { id }, new CategoryMapper());

    }

    /**
     * Method to get a list of all categories for a given config
     * @param configId Id of config to get categories for
     * @return A list of categories
     */
    @SuppressWarnings("unchecked")
    public static List<Category> getForConfig(int configId) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT c.categoryid, c.name, c.categorytypeid FROM " + TABLE_NAME + " c, "
                + DeviceConfigurationDao.CATEGORY_MAP_TABLE
                + " cc WHERE cc.configid = ? AND cc.categoryid = c.categoryid";

        return jdbcOps.query(sql, new Object[] { configId }, new CategoryMapper());

    }

    /**
     * Method to get a list of all categories of a given type
     * @param typeId Type of categories to get
     * @return A list of categories
     */
    @SuppressWarnings("unchecked")
    public static List<Category> getForType(int typeId) {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT categoryid, name, categorytypeid FROM " + TABLE_NAME
                + " WHERE categorytypeid = ?";

        // Load type once
        final Type type = TypeDao.getCategoryTypeForId(typeId);

        // Load, populate and return category list
        return jdbcOps.query(sql, new Object[] { typeId }, new CategoryMapper() {
            public Type getType(Integer typeId) {
                return type;
            }
        });

    }

    /**
     * Method to determine whether a category is assigned to any configurations
     * @param categoryId - Id of category in question
     * @return True if the category is assigned to any configs
     */
    public static boolean isCategoryInUse(int categoryId) {

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        String sql = "SELECT configid, categoryid FROM "
                + DeviceConfigurationDao.CATEGORY_MAP_TABLE + " WHERE categoryid = ?";

        SqlRowSet rowSet = (SqlRowSet) jdbcOps.query(sql,
                                                     new Object[] { categoryId },
                                                     new SqlRowSetResultSetExtractor());

        return rowSet.next();

    }

    /**
     * Mapping class to process a result set row into a category
     */
    private static class CategoryMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            Category category = new Category();
            category.setId(rs.getInt(1));
            category.setName(rs.getString(2));

            category.setType(this.getType((Integer) rs.getInt(3)));
            category.setItemList(this.getItemList(category.getId()));

            return category;
        }

        public Type getType(Integer typeId) throws SQLException {
            Type type = TypeDao.getCategoryTypeForId(typeId);

            return type;
        }

        public List<Item> getItemList(Integer categoryId) throws SQLException {
            return ItemDao.getForCategory(categoryId);
        }
    }

}
