package com.cannontech.database.data.device.configuration;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * Class which represents a device configuration item
 */
public class Item extends DBPersistent {

    private Integer id = null;
    private String name = null;
    private String value = null;
    boolean required = false;
    private int minValue = 0;
    private int maxValue = Integer.MAX_VALUE;
    private ItemValueType valueType = null;
    private String description = null;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxlength) {
        this.maxValue = maxlength;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minLength) {
        this.minValue = minLength;
    }

    public ItemValueType getValueType() {
        return valueType;
    }

    public void setValueType(String type) {
        this.valueType = ItemValueType.getItemValueType(type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
