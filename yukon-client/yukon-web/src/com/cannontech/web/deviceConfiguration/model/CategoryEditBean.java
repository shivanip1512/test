package com.cannontech.web.deviceConfiguration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.util.LazySortedMap;

public class CategoryEditBean {
    private Integer categoryId;
    private String categoryName;
    private String categoryType;
    private String description;
    private Map<String, String> categoryInputs = new HashMap<>();
    private List<String> assignments = new ArrayList<>();
    
    // This maps a schedule to its rates. The key for this map will be the schedule.
    private SortedMap<String, RateBackingBean> scheduleInputs = new LazySortedMap<>(String.class, RateBackingBean.class);
        
    public static final class RateBackingBean {
        // This maps the field to the rates.
        SortedMap<String, RateInput> rateInputs = new LazySortedMap<>(String.class, RateInput.class);

        public SortedMap<String, RateInput> getRateInputs() {
            return rateInputs;
        }

        public void setRateInputs(SortedMap<String, RateInput> rateInputs) {
            this.rateInputs = rateInputs;
        }
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public SortedMap<String, RateBackingBean> getScheduleInputs() {
        return scheduleInputs;
    }
    
    public void setScheduleInputs(SortedMap<String, RateBackingBean> scheduleInputs) {
        this.scheduleInputs = scheduleInputs;
    }

    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, String> getCategoryInputs() {
        return categoryInputs;
    }
    
    public void setCategoryInputs(Map<String, String> categoryInputs) {
        this.categoryInputs = categoryInputs;
    }

    public String getCategoryType() {
        return categoryType;
    }
    
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }
    
    public boolean isFieldHidden(String fieldName) {
        if (fieldName.contains("displayItem") && 
            !fieldName.toLowerCase().equals("displayItem1".toLowerCase())) {
            String val = categoryInputs.get(fieldName);
        
            if ("0".equals(val) || "SLOT_DISABLED".equals(val) ) {
                // This is a display item whose value is "Slot Disabled." Hide it since it isn't the first display item.
                return true;
            }
        }

        return false;
    }
    
    public boolean isScheduleRateHidden(String fieldName, String rateField) {
        RateBackingBean rate = scheduleInputs.get(fieldName);
        
        if (rate != null && "00:00".equals(rate.getRateInputs().get(rateField).getTime())) {
            return true;
        }
        
        return false;
    }
    
    public DeviceConfigCategory getModelObject() {
        // Description can be null.
        if (categoryName == null || categoryType == null || categoryInputs.isEmpty()) {
            throw new RuntimeException();
        }
        
        List<DeviceConfigCategoryItem> items = new ArrayList<>();
        for (Entry<String, String> entry : categoryInputs.entrySet()) {
            items.add(new DeviceConfigCategoryItem(categoryId, entry.getKey(), entry.getValue()));
        }
        
        // This is bad and for TOU only. Should be fixed.
        for (Entry<String, RateBackingBean> entry : scheduleInputs.entrySet()) {
            RateBackingBean backingBean = entry.getValue();
            
            for (Entry<String, RateInput> rateEntry : backingBean.getRateInputs().entrySet()) {
                // There is a value for time and a value for rate.
                String timeField = entry.getKey() + rateEntry.getKey();
                String rateField = entry.getKey() + rateEntry.getKey().replace("time", "rate"); // sigh.
                
                items.add(new DeviceConfigCategoryItem(categoryId, timeField, rateEntry.getValue().getTime()));
                items.add(new DeviceConfigCategoryItem(categoryId, rateField, rateEntry.getValue().getRate()));
            }
        }
        
        return new DeviceConfigCategory(categoryId, categoryType, categoryName, description, items);
    }
}
