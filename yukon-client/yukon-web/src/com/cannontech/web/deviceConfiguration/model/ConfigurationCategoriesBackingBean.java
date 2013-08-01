package com.cannontech.web.deviceConfiguration.model;

import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.LazyList;
import com.google.common.base.Objects;

/**
 * This backing bean stores the categories that have been chosen by the user.
 */
public class ConfigurationCategoriesBackingBean {
    private Integer configId; 
    private List<CategorySelection> categorySelections = LazyList.ofInstance(CategorySelection.class);
    
    public static class CategorySelection implements DisplayableEnum {
        private CategoryDisplay categoryDisplay;
        private String categoryName;
        private Integer categoryId;
        private String description;
        
        public CategorySelection() {
            categoryDisplay = new CategoryDisplay();
        }
        
        public CategorySelection(CategoryDisplay categoryDisplay) {
            this(categoryDisplay, null, null, null);
        }
        
        public CategorySelection(CategoryDisplay categoryDisplay, String categoryName, Integer categoryId, 
                                 String description) {
            this.categoryDisplay = categoryDisplay;
            this.categoryName = categoryName;
            this.categoryId = categoryId;
            this.description = description;
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
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.configs.category." + categoryDisplay.getCategoryType() + ".title";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            
            final CategorySelection other = (CategorySelection) obj;
            return Objects.equal(this.categoryDisplay, other.categoryDisplay) &&
                   Objects.equal(this.categoryName, other.categoryName) &&
                   Objects.equal(this.categoryId, other.categoryId) &&
                   Objects.equal(this.description, other.description);
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(categoryDisplay, categoryName, categoryId, description);
        }
    }
    
    public List<CategorySelection> getCategorySelections() {
        return categorySelections;
    }
    
    public void setCategorySelections(List<CategorySelection> categorySelections) {
        this.categorySelections = categorySelections;
    }
    
    public Integer getConfigId() {
        return configId;
    }
    
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
}
