package com.cannontech.web.deviceConfiguration.model;


public class CategoryDisplay {
    private String categoryType;
    private boolean existingCategories;

    public CategoryDisplay() {
    }
    
    public CategoryDisplay(String categoryType, boolean existingCategories) {
        this.categoryType = categoryType;
        this.existingCategories = existingCategories;
    }
    
    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public boolean getExistingCategories() {
        return existingCategories;
    }
    
    public void setExistingCategories(boolean existingCategories) {
        this.existingCategories = existingCategories;
    }
}
