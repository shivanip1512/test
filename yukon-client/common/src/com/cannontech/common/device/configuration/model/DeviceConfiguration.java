package com.cannontech.common.device.configuration.model;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cannontech.common.device.configuration.dao.DeviceConfigurationDao;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Class which represents a configuration for a device
 */
public class DeviceConfiguration extends DBPersistent implements CTIDbChange, EditorPanel {

    public static final String DB_CHANGE_CATEGORY = "config";
    public static final String DB_CHANGE_OBJECT_TYPE = "config";

    private Integer id = null;
    private String name = null;
    private Type type = null;
    private List<Category> categoryList = new LinkedList<Category>();

    public DeviceConfiguration() {
    }

    public DeviceConfiguration(Integer id) {
        this.id = id;
    }

    @Override
    public void add() throws SQLException {
        DeviceConfigurationDao.save(this);
    }

    @Override
    public void delete() throws SQLException {
        DeviceConfigurationDao.delete(this);
    }

    @Override
    public void retrieve() throws SQLException {
        DeviceConfiguration config = DeviceConfigurationDao.getForId(getId());

        this.setId(config.getId());
        this.setName(config.getName());
        this.setType(config.getType());
        this.setCategoryList(config.getCategoryList());
    }

    @Override
    public void update() throws SQLException {
        DeviceConfigurationDao.save(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    /**
     * Method to add a category to the category List
     * @param category - Category to add
     */
    public void addCategory(Category category) {

        if (category == null) {
            return;
        }
        if (categoryList == null) {
            categoryList = new LinkedList<Category>();
        }

        Category existingCategory = this.getCategoryByType(category.getTypeId());

        if (existingCategory != null) {
            categoryList.remove(existingCategory);
        }

        categoryList.add(category);
    }

    /**
     * Method to get a category from the category list
     * @param categoryId - Id of category to get
     * @return The category or null if not found
     */
    public Category getCategory(int categoryId) {

        if (categoryList == null) {
            return null;
        }

        Iterator categoryIter = categoryList.iterator();
        while (categoryIter.hasNext()) {
            Category category = (Category) categoryIter.next();
            if (category.getId() == categoryId) {
                return category;
            }
        }

        return null;

    }

    /**
     * Method to get a category from the category list by type
     * @param categoryTypeId - Id of category type to get
     * @return The category or null if not found
     */
    public Category getCategoryByType(int categoryTypeId) {

        if (categoryList == null) {
            return null;
        }

        Iterator categoryIter = categoryList.iterator();
        while (categoryIter.hasNext()) {
            Category category = (Category) categoryIter.next();
            if (category.getTypeId() == categoryTypeId) {
                return category;
            }
        }

        return null;

    }

    /**
     * Method to remove a category
     * @param categoryId Id of category to remove
     */
    public void removeCategory(int categoryId) {

        if (categoryList == null) {
            return;
        }

        Category category = null;
        Iterator categoryIter = categoryList.iterator();
        while (categoryIter.hasNext()) {
            category = (Category) categoryIter.next();
            if (category.getId() == categoryId) {
                break;
            }
        }

        categoryList.remove(category);

    }

    /**
     * Method to get the type id
     * @return The typeid or null if no type
     */
    public Integer getTypeId() {
        if (this.type != null) {
            return this.type.getId();
        }

        return null;
    }

    public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {
        DBChangeMsg[] msgs = new DBChangeMsg[1];
        msgs[0] = new DBChangeMsg(this.id,
                                  DBChangeMsg.CHANGE_CONFIG_DB,
                                  DeviceConfiguration.DB_CHANGE_CATEGORY,
                                  DB_CHANGE_OBJECT_TYPE,
                                  typeOfChange);

        return msgs;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
