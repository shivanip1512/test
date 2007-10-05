package com.cannontech.common.device.config.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.cannontech.web.input.Input;

public class ConfigurationTemplate {

    private String name = null;
    private String view = null;
    private String description = null;
    private Set<String> supportedDeviceSet = new HashSet<String>();
    private List<CategoryTemplate> categoryList = new ArrayList<CategoryTemplate>();

    private Properties dbNameMapping = null;

    public List<CategoryTemplate> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryTemplate> categoryList) {
        this.categoryList = categoryList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Set<String> getSupportedDeviceSet() {
        return supportedDeviceSet;
    }

    public void setSupportedDeviceSet(Set<String> supportedDeviceSet) {
        this.supportedDeviceSet = supportedDeviceSet;
    }

    public Properties getDbNameMapping() {
        return dbNameMapping;
    }

    public void setDbNameMapping(Properties dbNameMapping) {
        this.dbNameMapping = dbNameMapping;
    }

    public List<Input> getInputList() {

        List<Input> inputList = new ArrayList<Input>();

        for (CategoryTemplate category : categoryList) {
            inputList.addAll(category.getInputList());
        }

        return inputList;
    }

}
