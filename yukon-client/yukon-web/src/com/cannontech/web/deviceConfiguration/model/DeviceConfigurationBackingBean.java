package com.cannontech.web.deviceConfiguration.model;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class DeviceConfigurationBackingBean extends ConfigurationDeviceTypesBackingBean {
    private Integer configId;
    private String configName;
    private List<CategorySelection> categorySelections = LazyList.ofInstance(CategorySelection.class);
    
    public static class CategorySelection {
        private CategoryDisplay categoryDisplay;
        private String categoryName;
        private Integer categoryId;
        
        public CategorySelection() {
            categoryDisplay = new CategoryDisplay();
        }
        
        public CategorySelection(CategoryDisplay categoryDisplay) {
            this(categoryDisplay, null, null);
        }
        
        public CategorySelection(CategoryDisplay categoryDisplay, String categoryName, Integer categoryId) {
            this.categoryDisplay = categoryDisplay;
            this.categoryName = categoryName;
            this.categoryId = categoryId;
        }
        
        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }
        
        public Integer getCategoryId() {
            return categoryId;
        }
        
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        
        public String getCategoryName() {
            return categoryName;
        }

        public CategoryDisplay getCategoryDisplay() {
            return categoryDisplay;
        }
        
        public void setCategoryDisplay(CategoryDisplay categoryDisplay) {
            this.categoryDisplay = categoryDisplay;
        }
    }
    
    public Integer getConfigId() {
        return configId;
    }
    
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
    
    public List<CategorySelection> getCategorySelections() {
        return categorySelections;
    }
    
    public void setCategorySelections(List<CategorySelection> categorySelections) {
        this.categorySelections = categorySelections;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
