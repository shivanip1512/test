package com.cannontech.common.device.config.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.google.common.collect.Lists;

public class MCT440Configuration extends ConfigurationBase {

    private List<Integer> displayItems;
    private final int NUM_OF_DISPLAY_ITEMS = 26;
    
    public MCT440Configuration(){
        displayItems = Lists.newArrayList(Collections.nCopies(NUM_OF_DISPLAY_ITEMS, 0));
    }
    
    public List<Integer> getDisplayItems() {
        return displayItems;
    }

    public void setDisplayItems(List<Integer> displayItems) {
        this.displayItems = displayItems;
    }

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT440;
    }
}
