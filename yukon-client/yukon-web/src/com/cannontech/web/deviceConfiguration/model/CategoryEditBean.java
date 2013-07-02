package com.cannontech.web.deviceConfiguration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.map.LazyMap;

import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.google.common.collect.Maps;

public class CategoryEditBean {
    private Integer categoryId;
    private String categoryName;
    private String categoryType;
    private Map<String, String> categoryInputs = new HashMap<>();
    
    // This maps a schedule to its rates. The key for this map will be the schedule.
    private Map<String, RateBackingBean> scheduleInputs =
            LazyMap.decorate(Maps.newTreeMap(), FactoryUtils.instantiateFactory(RateBackingBean.class));
        
    public static final class RateBackingBean {
        // This maps the field to the rates.
        Map<String, RateInput> rateInputs =
            LazyMap.decorate(Maps.newTreeMap(), FactoryUtils.instantiateFactory(RateInput.class));
        
        public Map<String, RateInput> getRateInputs() {
            return rateInputs;
        }
        
        public void setRateInputs(Map<String, RateInput> rateInputs) {
            this.rateInputs = rateInputs;
        }
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public Map<String, RateBackingBean> getScheduleInputs() {
        return scheduleInputs;
    }
    
    public void setScheduleInputs(Map<String, RateBackingBean> scheduleInputs) {
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
    
    public DeviceConfigCategory getModelObject() {
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
        
        return new DeviceConfigCategory(categoryId, categoryType, categoryName, items);
    }
}
