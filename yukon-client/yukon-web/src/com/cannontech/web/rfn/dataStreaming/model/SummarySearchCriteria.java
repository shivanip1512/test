package com.cannontech.web.rfn.dataStreaming.model;

import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class SummarySearchCriteria {
    private List<Integer> selectedGatewayIds;
    private int selectedConfiguration;
    private List<String> selectedAttributes;
    private int selectedInterval;
    private Double minLoadPercent;
    private Double maxLoadPercent;
    
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

    public Double getMinLoadPercent() {
        return minLoadPercent;
    }

    public void setMinLoadPercent(Double minLoadPercent) {
        this.minLoadPercent = minLoadPercent;
    }

    public Double getMaxLoadPercent() {
        return maxLoadPercent;
    }

    public void setMaxLoadPercent(Double maxLoadPercent) {
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
        
    public boolean isGatewaySelected() {
        try {
            return selectedGatewayIds.get(0) != -1;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<BuiltInAttribute> getBuiltInAttributes() {
        try {
            if (selectedAttributes.get(0).equals("-1")) {
                return null;
            }
            return selectedAttributes.stream().filter(attribute -> !attribute.equals("-1")).map(
                attribute -> BuiltInAttribute.valueOf(attribute)).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
}
