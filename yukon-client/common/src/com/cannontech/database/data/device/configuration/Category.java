package com.cannontech.database.data.device.configuration;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.cache.functions.CategoryDao;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Class which represents a device configuration category
 */
public class Category extends DBPersistent implements CTIDbChange, Comparable, EditorPanel {

    public static final String DB_CHANGE_OBJECT_TYPE = "Category";

    private Integer id = null;
    private String name = null;
    private Type type = null;
    private List<Item> itemList = new LinkedList<Item>();

    private boolean selected = false;

    public Category() {
    }

    public Category(Integer id) {
        this.id = id;
    }

    @Override
    public void add() throws SQLException {
        CategoryDao.save(this);
    }

    @Override
    public void delete() throws SQLException {
        CategoryDao.delete(this);
    }

    @Override
    public void retrieve() throws SQLException {
        Category category = CategoryDao.getForId(getId());

        this.setId(category.getId());
        this.setName(category.getName());
        this.setType(category.getType());
        this.setItemList(category.getItemList());
    }

    @Override
    public void update() throws SQLException {
        CategoryDao.save(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
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

    public Integer getTypeId() {
        if (this.type != null) {
            return this.type.getId();
        }

        return null;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Method to add an Item to the item List
     * @param item - Item to add
     */
    public void addItem(Item item) {

        if (item.getId() == null || this.getItem(item.getId()) == null) {
            this.itemList.add(item);
        }

    }

    /**
     * Method to get an item from the item list
     * @param itemId - Id of item to get
     * @return The item or null if not found
     */
    public Item getItem(int itemId) {

        if (itemList == null) {
            return null;
        }

        Iterator itemIter = itemList.iterator();
        while (itemIter.hasNext()) {
            Item item = (Item) itemIter.next();
            if (item.getId() == itemId) {
                return item;
            }
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

    public int compareTo(Object o) {

        if (o == null || !(o instanceof Category)) {
            return 1;
        }

        return this.name.compareTo(((Category) o).getName());
    }

}
