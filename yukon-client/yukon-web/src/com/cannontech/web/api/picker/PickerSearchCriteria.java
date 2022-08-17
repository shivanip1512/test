package com.cannontech.web.api.picker;

public class PickerSearchCriteria {
    
    private String type;
    private String queryString;
    private Integer startCount;
    private Integer count;
    private String extraArgs;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getQueryString() {
        return queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    public Integer getStartCount() {
        return startCount;
    }
    public void setStartCount(Integer startCount) {
        this.startCount = startCount;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public String getExtraArgs() {
        return extraArgs;
    }
    public void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
    }

}
