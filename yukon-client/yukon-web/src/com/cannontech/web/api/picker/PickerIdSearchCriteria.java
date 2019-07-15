package com.cannontech.web.api.picker;

public class PickerIdSearchCriteria {
    
    private String type;
    private Integer[] initialIds;
    private String extraArgs;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer[] getInitialIds() {
        return initialIds;
    }
    public void setInitialIds(Integer[] initialIds) {
        this.initialIds = initialIds;
    }
    public String getExtraArgs() {
        return extraArgs;
    }
    public void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
    }

}
