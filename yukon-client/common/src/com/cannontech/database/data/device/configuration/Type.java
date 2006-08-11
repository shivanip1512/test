package com.cannontech.database.data.device.configuration;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * Class which represents a type of device configuration, category or item
 */
public class Type extends DBPersistent implements Comparable {

    private Integer id = null;
    private String name = null;
    private String description = null;
    private String group = null;
    private CategoryLevel level = null;

    public Type() {
    }

    public Type(Integer id) {
        this.id = id;
    }

    @Override
    public void add() throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    @Override
    public void delete() throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    @Override
    public void retrieve() throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    @Override
    public void update() throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public CategoryLevel getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = CategoryLevel.getCategoryLevel(level);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int compareTo(Object o) {

        if (o == null || !(o instanceof Type)) {
            return 1;
        }

        String thisVal = (this.group == null) ? this.name : this.group;
        String oVal = (((Type) o).getGroup() == null) ? ((Type) o).getName()
                : ((Type) o).getGroup();

        int val = thisVal.compareTo(oVal);
        return val;
    }

}
