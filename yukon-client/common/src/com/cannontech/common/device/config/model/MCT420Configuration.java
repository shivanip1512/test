package com.cannontech.common.device.config.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.google.common.collect.Lists;

public class MCT420Configuration extends ConfigurationBase {
    private List<Integer> displayItems;
    private int lcdCycleTime = 8;
    private boolean disconnectDisplayDisabled = false;
    private int displayDigits = 5;
    private final int NUM_OF_DISPLAY_ITEMS = 26;
    
    public MCT420Configuration(){
        displayItems = Lists.newArrayList(Collections.nCopies(NUM_OF_DISPLAY_ITEMS, 0));
    }
    
    public List<Integer> getDisplayItems() {
        return displayItems;
    }

    public void setDisplayItems(List<Integer> displayItems) {
        this.displayItems = displayItems;
    }
    
    public int getLcdCycleTime() {
        return lcdCycleTime;
    }
    
    public void setLcdCycleTime(int lcdCycleTime) {
        this.lcdCycleTime = lcdCycleTime;
    }
    
    public boolean isDisconnectDisplayDisabled() {
        return disconnectDisplayDisabled;
    }
    
    public void setDisconnectDisplayDisabled(boolean disconnectDisplayDisabled) {
        this.disconnectDisplayDisabled = disconnectDisplayDisabled;
    }
    
    public void setDisplayDigits(int displayDigits) {
        this.displayDigits = displayDigits;
    }
    
    public int getDisplayDigits() {
        return displayDigits;
    }
    
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT420;
    }

}