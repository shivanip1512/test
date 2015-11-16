package com.cannontech.web.deviceConfiguration.model;

import com.google.common.base.Objects;

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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null ) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final CategoryDisplay other = (CategoryDisplay) obj;
        return Objects.equal(this.categoryType, other.categoryType) && 
               Objects.equal(this.existingCategories, other.existingCategories);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(categoryType, existingCategories);
    }

    @Override
    public String toString() {
        return String.format("CategoryDisplay [categoryType=%s, existingCategories=%s]", categoryType, existingCategories);
    }

}
