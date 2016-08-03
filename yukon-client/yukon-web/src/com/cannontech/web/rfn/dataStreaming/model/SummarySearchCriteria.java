package com.cannontech.web.rfn.dataStreaming.model;

import java.util.List;

public class SummarySearchCriteria {
    private List<Integer> selectedGatewayIds;
    private int selectedConfiguration;
    private List<String> selectedAttributes;
    private int selectedInterval;
    private double minLoadPercent;
    private double maxLoadPercent = 100;
    
    public SummarySearchCriteria() {
        
    }

    public List<Integer> getSelectedGatewayIds() {
        return selectedGatewayIds;
    }

    public void setSelectedGatewayIds(List<Integer> selectedGatewayIds) {
        this.selectedGatewayIds = selectedGatewayIds;
    }

    public int getSelectedConfiguration() {
        return selectedConfiguration;
    }

    public void setSelectedConfiguration(int selectedConfiguration) {
        this.selectedConfiguration = selectedConfiguration;
    }

    public List<String> getSelectedAttributes() {
        return selectedAttributes;
    }

    public void setSelectedAttributes(List<String> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    public int getSelectedInterval() {
        return selectedInterval;
    }

    public void setSelectedInterval(int selectedInterval) {
        this.selectedInterval = selectedInterval;
    }

    public double getMinLoadPercent() {
        return minLoadPercent;
    }

    public void setMinLoadPercent(double minLoadPercent) {
        this.minLoadPercent = minLoadPercent;
    }

    public double getMaxLoadPercent() {
        return maxLoadPercent;
    }

    public void setMaxLoadPercent(double maxLoadPercent) {
        this.maxLoadPercent = maxLoadPercent;
    }
    
    public boolean isConfigCriteriaEmpty(){
       return !isConfigSelected() || !isConfigSelected();
    }
    
    public boolean isConfigIntervalSelected(){
        return selectedInterval > 0;
     }
    
    public boolean isConfigSelected(){
        return selectedConfiguration > 0;
    }
    
    public boolean isConfigAttributesSelected() {
        try {
            return !"-1".equals(selectedAttributes.get(0));
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isGatewaySelected() {
        try {
            return -1 != selectedGatewayIds.get(0);
        } catch (Exception e) {
            return false;
        }
    }
}
