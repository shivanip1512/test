package com.cannontech.common.device.configuration.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.db.DBPersistent;

/**
 * Class which represents a device configuration item
 */
public class Item extends DBPersistent {

    private Integer id = null;
    private String name = null;
    private String displayName = null;
    private String value = null;
    boolean required = false;
    private int minValue = 0;
    private int maxValue = Integer.MAX_VALUE;
    private ItemValueType valueType = null;
    private String defaultValue = null;
    private String description = null;

    private List<String> possibleValueList = null;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPossibleValueList() {
        return possibleValueList;
    }

    public void setPossibleValueList(List<String> possibleValueList) {
        this.possibleValueList = possibleValueList;
    }

    /**
     * Method to add a value to the list of possible values
     * @param value - Value to add
     */
    public void addPossibleValue(String value) {
        if (this.possibleValueList == null) {
            this.possibleValueList = new ArrayList<String>();
        }

        this.possibleValueList.add(value);
    }

    /**
     * Method to set the value to the defaultValue
     */
    public void restoreDefaultValue() {
        this.value = this.defaultValue;
    }

    @Override
    public Object clone() {

        Item newItem = new Item();

        newItem.setId(this.id);
        newItem.setName(this.name);
        newItem.setDisplayName(this.displayName);
        newItem.setMaxValue(this.maxValue);
        newItem.setMinValue(this.minValue);
        newItem.setRequired(this.required);
        newItem.setValue(this.value);
        newItem.setValueType(this.valueType.toString());

        if (this.possibleValueList != null) {
            Iterator<String> iter = this.possibleValueList.iterator();
            while (iter.hasNext()) {
                newItem.addPossibleValue(iter.next());
            }
        }

        return newItem;
    }

}
